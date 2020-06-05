package net.splitcells.dem.data.set.list;

import java.util.ArrayList;
import java.util.List;

public final class ListFI implements ListF {

	@Override
	public <T> List<T> list() {
		return new ArrayList<>();
	}

	@Override
	public <T> List<T> list(List<T> arg) {
		return new ArrayList<>(arg);
	}
}
