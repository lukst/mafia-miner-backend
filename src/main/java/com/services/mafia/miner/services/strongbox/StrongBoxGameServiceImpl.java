package com.services.mafia.miner.services.strongbox;

import com.services.mafia.miner.dto.strongbox.StrongboxGameDTO;
import com.services.mafia.miner.dto.strongbox.StrongboxHistoryDTO;
import com.services.mafia.miner.entity.strongbox.Combination;
import com.services.mafia.miner.entity.strongbox.StrongBoxGame;
import com.services.mafia.miner.entity.strongbox.StrongboxHistory;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.strongbox.CombinationRepository;
import com.services.mafia.miner.repository.strongbox.StrongBoxGameRepository;
import com.services.mafia.miner.repository.strongbox.StrongboxHistoryRepository;
import com.services.mafia.miner.services.RateLimitingCacheService;
import com.services.mafia.miner.services.transaction.TransactionService;
import com.services.mafia.miner.services.user.UserService;
import com.services.mafia.miner.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StrongBoxGameServiceImpl implements StrongBoxGameService {
    private final StrongBoxGameRepository strongBoxGameRepository;
    private final StrongboxHistoryRepository strongboxHistoryRepository;
    private final CombinationRepository combinationRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final RateLimitingCacheService rateLimitingCacheService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public StrongboxGameDTO findStrongboxCurrentGame() {
        return modelMapper.map(strongBoxGameRepository.findTopByOrderByLastResetTimeDesc().orElseThrow(() ->
                        new InvalidInputException("No Strongbox was created yet")),
                StrongboxGameDTO.class);
    }

    @Override
    @Transactional
    public StrongboxGameDTO play(HttpServletRequest request, Long strongboxId, Long combinationId) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        rateLimitingCacheService.checkRateLimit(user.getId(), 1);
        StrongBoxGame lastStrongBoxGame = strongBoxGameRepository.findTopByOrderByLastResetTimeDesc()
                .orElseThrow(() -> new InvalidInputException("No strongbox game found"));
        StrongBoxGame strongBoxGame = strongBoxGameRepository.findById(strongboxId).orElseThrow(() ->
                new InvalidInputException("No strongbox found"));
        if(!lastStrongBoxGame.getId().equals(strongBoxGame.getId())){
            throw new InvalidInputException("This strongbox is already closed");
        }
        if(strongBoxGame.getCurrentMultiplier() <= 15){
            throw new InvalidInputException("This strongbox is already closed");
        }
        Combination combination = strongBoxGame.getCombinations().stream()
                .filter(c -> c.getId().equals(combinationId) && !c.isAttempted())
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Invalid or already attempted combination"));

        BigDecimal playPrice = strongBoxGame.getAttemptCost();
        userService.subtractUserMCOIN(user, playPrice);
        StrongBoxGame responseStrongbox;
        BigDecimal winningPrice = BigDecimal.ZERO;
        boolean userWon = false;
        if (strongBoxGame.getWinningCombination() == combination.getNumber()) {
            winningPrice = playPrice.multiply(BigDecimal.valueOf(strongBoxGame.getCurrentMultiplier()));
            StrongboxHistory strongboxHistory = StrongboxHistory.builder()
                    .user(user)
                    .game(strongBoxGame)
                    .winningCombination(combination.getNumber())
                    .winningMultiplier(strongBoxGame.getCurrentMultiplier())
                    .winAmount(winningPrice)
                    .winTime(LocalDateTime.now())
                    .build();
            strongboxHistoryRepository.save(strongboxHistory);
            user.setMcoinBalance(user.getMcoinBalance().add(winningPrice));
            transactionService.saveTransactionRecordMCOIN(
                    TransactionType.WIN_STRONGBOX,
                    user,
                    winningPrice,
                    Constants.STRONGBOX_ATTEMPT + " with number " + combination.getNumber() + " won!");
            responseStrongbox = generateStrongboxGame();
            userWon = true;
            userService.save(user);
        } else {
            transactionService.saveTransactionRecordMCOIN(
                    TransactionType.PLAY_STRONGBOX,
                    user,
                    playPrice.negate(),
                    Constants.STRONGBOX_ATTEMPT + " with number " + combination.getNumber() + " failed");
            responseStrongbox = strongBoxGame;
        }
        combination.setAttempted(true);
        strongBoxGame.setCurrentMultiplier(strongBoxGame.getCurrentMultiplier() - 1);
        strongBoxGame.setAttemptsCount(strongBoxGame.getAttemptsCount() + 1);
        if(responseStrongbox.getCurrentMultiplier() <= 15 && strongBoxGame.getWinningCombination() != combination.getNumber()){
            responseStrongbox.resetGame();
        }
        StrongBoxGame updatedGame = strongBoxGameRepository.save(responseStrongbox);
        combinationRepository.save(combination);
        StrongboxGameDTO dtoResponse = modelMapper.map(updatedGame, StrongboxGameDTO.class);
        dtoResponse.setWon(userWon);
        dtoResponse.setMcoinWon(winningPrice);
        return dtoResponse;
    }

    @Override
    public List<StrongboxHistoryDTO> getLast5StrongboxHistory() {
        List<StrongboxHistory> strongboxHistories = strongboxHistoryRepository.findTop5ByOrderByWinTimeDesc();
        List<StrongboxHistoryDTO> responseStrongBoxHistory = new ArrayList<>();
        for (StrongboxHistory strongboxHistory: strongboxHistories) {
            StrongboxHistoryDTO response = modelMapper.map(strongboxHistory, StrongboxHistoryDTO.class);
            response.setWinningWallet(strongboxHistory.getUser().getWalletAddress());
            responseStrongBoxHistory.add(response);
        }
        return responseStrongBoxHistory;
    }

    private StrongBoxGame generateStrongboxGame() {
        StrongBoxGame game = StrongBoxGame.builder()
                .winningCombination((int) (Math.random() * 100))
                .build();
        game.initializeCombinations();
        return strongBoxGameRepository.save(game);
    }
}
