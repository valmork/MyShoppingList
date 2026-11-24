package com.example.myshoppinglist.data

import com.example.myshoppinglist.domain.ShopItem

class ShopListMapper {

    fun mapEntityToDbModel(shopItem: ShopItem): ShopItemDbModel = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled
    )

    fun mapDbModelToEntity(dbModel: ShopItemDbModel): ShopItem = ShopItem(
        id = dbModel.id,
        name = dbModel.name,
        count = dbModel.count,
        enabled = dbModel.enabled
    )


    fun mapListDbModelToListEntity(list: List<ShopItemDbModel>): List<ShopItem> = list.map {
        mapDbModelToEntity(it)
    }
}