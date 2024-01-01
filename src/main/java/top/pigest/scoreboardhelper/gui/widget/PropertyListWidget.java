package top.pigest.scoreboardhelper.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import org.jetbrains.annotations.Nullable;
import top.pigest.scoreboardhelper.config.property.Property;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardHelperConfigScreen;

import java.util.List;
import java.util.Map;

public class PropertyListWidget extends ElementListWidget<PropertyListWidget.WidgetEntry> {
    private final ScoreboardHelperConfigScreen parent;
    public PropertyListWidget(MinecraftClient minecraftClient, ScoreboardHelperConfigScreen screen) {
        super(minecraftClient, screen.width, screen.height, 32, screen.height - 32, 25);
        parent = screen;
        this.addAll(parent.getConfig().getProperties());
    }

    public void addPropertyEntry(Property<?> property1, @Nullable Property<?> property2) {
        this.addEntry(WidgetEntry.create(this.width, property1, property2));
    }

    public void addAll(List<Property<?>> properties) {
        for (int i = 0; i < properties.size(); i += 2) {
            addPropertyEntry(properties.get(i), i < properties.size() - 1 ? properties.get(i + 1) : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    protected static class WidgetEntry extends ElementListWidget.Entry<WidgetEntry> {
        private final List<ClickableWidget> widgets;

        private WidgetEntry(Map<Property<?>, ClickableWidget> propertiesToWidgets) {
            this.widgets = ImmutableList.copyOf(propertiesToWidgets.values());
        }

        public static WidgetEntry create(int width, Property<?> firstProperty, @Nullable Property<?> secondProperty) {
            ClickableWidget clickableWidget = firstProperty.createWidget(width / 2 - 205, 0, 200);
            if (secondProperty == null) {
                return new WidgetEntry(ImmutableMap.of(firstProperty, clickableWidget));
            }
            return new WidgetEntry(ImmutableMap.of(firstProperty, clickableWidget, secondProperty, secondProperty.createWidget(width / 2 - 205 + 210, 0, 200)));
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return widgets;
        }

        @Override
        public List<? extends Element> children() {
            return widgets;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.widgets.forEach(
                    widget -> {
                        widget.setY(y);
                        widget.render(context, mouseX, mouseY, tickDelta);
                    }
            );
        }
    }
}
