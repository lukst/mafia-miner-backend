package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface NFTService {
    NFTDTO mintNFT(HttpServletRequest request, Long catalogID, boolean bnbMint);
}
