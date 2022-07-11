package mine.block.glass.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;

public interface ArrayComponent<T> extends AutoSyncedComponent {
    ArrayList<T> getValue();
    void addValue(T value);
    void setValue(ArrayList<T> value);
    void removeValue(T value);
}
