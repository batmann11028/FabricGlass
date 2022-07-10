package mine.block.glass.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.HashMap;

public interface HashMapComponent<A, B> extends AutoSyncedComponent {
    HashMap<A, B> getValue();
    void setValue(HashMap<A, B> value);
}
