package com.mazadak.product_catalog.dto.event;


import com.mazadak.product_catalog.dto.client.AuctionResponse;

import java.util.List;

public record AuctionEndedEvent(AuctionResponse auction,
                                Object bidders,
                                Object watchlist) {
}
