package mine.block.glass.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;

public interface StringArrayComponent extends AutoSyncedComponent {
    ArrayList<String> getValue();
    void addValue(String value);
    void setValue(ArrayList<String> value);
    void removeValue(String value);
}
