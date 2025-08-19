package sunsetsatellite.catalyst.multipart.api;

import sunsetsatellite.catalyst.core.util.Direction;

import java.util.HashMap;

public interface SupportsMultiparts {

	HashMap<Direction, Multipart> getParts();
}
