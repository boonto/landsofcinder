package de.loc.input.userinterface;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.quest.Quest;
import de.loc.quest.QuestLog;
import de.loc.tools.Constants;

public class QuestLogWindow extends MenuWindow implements EventListener {

    private final QuestLog questLog;

    public QuestLogWindow(UserInterface ui, QuestLog questLog) {
        super(ui, Type.MIDDLE);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.values());

        this.questLog = questLog;

        //        questLog.addListener(new EventListener() {
        //            @Override
        //            public void update(Event e) {
        //                updateQuestLog();
        //            }
        //        });
    }

    private void updateQuestLog() {
        this.mainTable.clearChildren();
        for ( Quest quest : this.questLog.getActiveQuests().values() ) {
            this.addQuestLogEntry(quest);
        }
    }

    private void addQuestLogEntry(Quest quest) {
        Table item = new Table();
        item.padTop((float) 50.0);
        Label name = new Label(quest.NAME, this.getSkin());
        //Label questgiver = new Label(quest.getClientName(), getSkin());
        Label description = new Label("\n" + quest.description, this.getSkin());

        description.setWrap(true);

        float width = this.getWidth() - Constants.UI_IMAGE_SIZE;

        item.add(name).width(width / 2);
        //item.add(questgiver).width(width/2);
        item.row();
        item.add(description).width(width).colspan(2).left();

        this.mainTable.top().left();

        this.mainTable.add(item).width(width);
    }

    @Override
    public void update(Event e) {

        this.updateQuestLog();
    }
}
