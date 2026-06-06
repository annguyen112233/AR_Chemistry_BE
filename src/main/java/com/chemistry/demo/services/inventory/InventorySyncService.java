package com.chemistry.demo.services.inventory;

import com.chemistry.demo.dto.response.inventory.SyncInventoryResponse;

public interface InventorySyncService {
    SyncInventoryResponse syncKitInventory(String kitId);
}
