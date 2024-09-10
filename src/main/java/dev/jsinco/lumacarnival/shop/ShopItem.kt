package dev.jsinco.lumacarnival.shop

import dev.jsinco.lumacarnival.Util
import org.bukkit.inventory.ItemStack

class ShopItem (
    val item: ItemStack,
    val command: String,
    val tokenPrice: Int,
    val slot: Int
) {

    val displayItem: ItemStack = item.clone()

    init {
        val itemMeta = displayItem.itemMeta
        val lore = itemMeta.lore() ?: mutableListOf()
        lore.addAll(
            Util.mml(
                "",
                "<#8EC4F7>✿ <#B4B7E8>C<#C7B0E1>l<#D9A9DA>i<#ECA3D2>c<#FF9CCB>k <#EFC0B2>t<#E7D1A6>o <#D7F58D>p<#DEF78D>u<#E4F88C>r<#EBFA8C>c<#F2FB8B>h<#F8FD8B>a<#FFFE8A>s<#FFF583>e <#FFE474>f<#FFDC6C>o<#FFD365>r<dark_gray>:",
                "<gradient:#8ec4f7:#ff9ccb>• $tokenPrice </gradient><gradient:#ff9ccb:#d7f58d>Carni</gradient><gradient:#d7f58d:#fffe8a>val T</gradient><gradient:#fffe8a:#ffd365>okens</gradient>"
            ) // •
        )
        itemMeta.lore(lore)
        displayItem.itemMeta = itemMeta
    }
}