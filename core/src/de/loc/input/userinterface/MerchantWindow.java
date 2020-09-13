package de.loc.input.userinterface;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.loc.item.InventoryComponent;
import de.loc.item.ItemComponent;
import de.loc.item.MerchantComponent;
import de.loc.item.StatComponent;
import de.loc.tools.Constants;

public class MerchantWindow extends MenuWindow {

    private final ComponentMapper<ItemComponent> itemMapper;

    public MerchantWindow(UserInterface ui) {
        super(ui, Type.MIDDLE);
        this.itemMapper = ComponentMapper.getFor(ItemComponent.class);
    }

    public void show(final MerchantComponent merchantC, final InventoryComponent playerInventory) {
        this.mainTable.clearChildren();

        int index = 0;
        for ( Entity e : merchantC.merchantInventory.contents.values() ) {
            ItemComponent ic = this.itemMapper.get(e);
            String itemName = ic.name;
            String itemDescription = ic.description;
            String iconPath = ic.iconPath;
            String price = String.valueOf(merchantC.priceList.get(ic.name));
            String itemStat;
            if ( e.getComponent(StatComponent.class) != null ) {
                String statType = e.getComponent(StatComponent.class).stats.get(0).type.toString().toLowerCase();
                statType = Character.toString(statType.charAt(0)).toUpperCase() + statType.substring(1);
                String statValue = String.valueOf(e.getComponent(StatComponent.class).stats.get(0).value);
                itemStat = statType + " +" + statValue;
            } else {
                itemStat = "";
            }

            final Actor
                actor =
                this.createInventoryEntry(itemName + "\nPreis: " + price, itemDescription, Constants.ICON_PATH + iconPath, itemStat, ic.getAmount(), itemName);

            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Entity itemToBuy = merchantC.merchantInventory.contents.get(actor.getName());
                    Entity money = playerInventory.contents.get(Constants.NAME_OF_CURRENCY);
                    if ( money != null ) {
                        ItemComponent iMoney = MerchantWindow.this.itemMapper.get(money);
                        ItemComponent iItemToBuy = MerchantWindow.this.itemMapper.get(itemToBuy);
                        int price = merchantC.priceList.get(iItemToBuy.name);
                        if ( iMoney.getAmount() >= price ) {
                            playerInventory.addItem(itemToBuy, 1);
                            playerInventory.removeItem(money, price);
                            merchantC.merchantInventory.removeItem(itemToBuy, 1);
                            MerchantWindow.this.show(merchantC, playerInventory);
                        }
                    }

                }
            });

            index++;
        }
    }

}
