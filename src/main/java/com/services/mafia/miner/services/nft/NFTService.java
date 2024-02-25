package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface NFTService {
    NFTDTO mintNFT(HttpServletRequest request, Long catalogID, boolean bnbMint);
    Page<NFTDTO> filterNftsForUser(HttpServletRequest request, int page, int size);
    NFTDTO play(HttpServletRequest request, Long nftId);
}
