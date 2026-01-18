package me.axieum.mcmod.authme.api.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionScreen extends Screen {
    private static final int CHAR_SIZE = 15;
    private static final int GAP_BETWEEN_BUTTONS = 5;
    private static final int HEIGHT = 20;

    private final Screen successScreen;

    protected UserSelectionScreen(Screen successScreen) {
        super(Component.translatable("gui.authme.userselect.title"));
        this.successScreen = successScreen;
    }

    @Override
    protected void init() {
        List<String> usernames = new ArrayList<>();
        usernames.add("somebody");
        usernames.add("nobody");
        usernames.add("everybody");

        Button lastButton = null;

        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            // TODO: Images perhaps just a steve but maybe the player skin head later on
            Button button = new Button.Builder(Component.literal(username), (press) ->
                    minecraft.setScreen(successScreen))
                    .bounds(
                    i + (lastButton == null ? GAP_BETWEEN_BUTTONS : lastButton.getX() + lastButton.getWidth() + GAP_BETWEEN_BUTTONS),
                            0,
                            CHAR_SIZE * username.length(),
                            HEIGHT
            ).build();
            addRenderableWidget(button);

            lastButton = button;
       }
    }
}
