package sunsetsatellite.catalyst.core.util.section;

public interface SideInteractable {

	default boolean needsItemToShowOutline() {
		return true;
	}

	default boolean alwaysShowOutlineWhenHeld() {
		return false;
	}
}
