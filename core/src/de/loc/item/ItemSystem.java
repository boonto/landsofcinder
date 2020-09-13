package de.loc.item;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import de.loc.combat.CombatComponent;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.graphics.RenderableComponent;
import de.loc.input.ClickableComponent;

public class ItemSystem extends LandsOfCinderSystem implements EventListener {

    private ImmutableArray<Entity> itemEntities;
    private ImmutableArray<Entity> inventoryEntities;
    private ImmutableArray<Entity> merchantEntities;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private final ComponentMapper<ItemComponent> itemMapper;
    private final ComponentMapper<StatComponent> statMapper;
    private final ComponentMapper<EquipmentComponent> equipmentMapper;
    private final ComponentMapper<CombatComponent> combatMapper;
    private final ComponentMapper<InventoryComponent> inventoryMapper;

    private Entity playerEntity;
    public InventoryComponent playerInventory;

    public ItemSystem() {

        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        this.itemMapper = ComponentMapper.getFor(ItemComponent.class);
        this.statMapper = ComponentMapper.getFor(StatComponent.class);
        this.equipmentMapper = ComponentMapper.getFor(EquipmentComponent.class);
        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);
        this.inventoryMapper = ComponentMapper.getFor(InventoryComponent.class);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.CONSUME_ITEM, EventSystem.EventType.EQUIP_ITEM);
    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
        this.itemEntities = engine.getEntitiesFor(Family.all(ItemComponent.class, ClickableComponent.class).get());
        this.inventoryEntities = engine.getEntitiesFor(Family.all(InventoryComponent.class, ClickableComponent.class).exclude(CombatComponent.class).get());
        this.merchantEntities = engine.getEntitiesFor(Family.all(ClickableComponent.class, MerchantComponent.class).get());

        this.playerEntity = LevelManager.getInstance().getPlayerEntity();
        this.playerInventory = this.inventoryMapper.get(this.playerEntity);
    }

    public void update(float deltaTime) {

        for ( Entity entity : this.itemEntities ) {

            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                ItemComponent item = this.itemMapper.get(entity);
                // add item to inventory
                InventoryComponent playerInventory = this.inventoryMapper.get(this.playerEntity);
                playerInventory.addItem(entity, item.getAmount());

                EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.FETCH_EVENT, item.name));

                entity.remove(RenderableComponent.class);
                entity.remove(PositionComponent.class);
                entity.remove(ClickableComponent.class);

                this.getEngine().removeEntity(entity);

                for ( Entity playerItems : playerInventory.contents.values() ) {
                    Gdx.app.log("ITEM", playerItems.getComponent(ItemComponent.class).name);
                }
            }
        }
        for ( Entity entity : this.inventoryEntities ) {

            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                Gdx.app.log("ITEM", "JO HAST DAS DINGE ANGEKLICKT!");
                EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.SHOW_INVENTORY_LIST, entity));
            }
        }
        for ( Entity entity : this.merchantEntities ) {
            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                Gdx.app.log("ITEM", "HANDELN?");
                EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.SHOW_MERCHANT_WINDOW, entity));
            }
        }
    }

    @Override
    public void update(Event e) {
        Entity item = (Entity) e.args[0];
        Entity target = (Entity) e.args[1];

        switch ( e.eventType ) {
            case EQUIP_ITEM:
                this.equipmentMapper.get(target).equip(item);
                break;
            case CONSUME_ITEM:
                StatComponent statComponent = this.statMapper.get(item);

                StatManager.modifyStat(this.combatMapper.get(target), statComponent);
                break;
        }
        this.getEngine().removeEntity(item);
    }

    @Override
    public void reset() {
        this.playerEntity = LevelManager.getInstance().getPlayerEntity();
        LevelManager.getInstance().getPlayerEntity().add(this.playerInventory);
    }
}
