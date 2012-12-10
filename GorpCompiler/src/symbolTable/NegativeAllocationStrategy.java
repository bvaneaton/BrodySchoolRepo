package symbolTable;

import java.util.ArrayList;
import java.util.List;

public class NegativeAllocationStrategy implements AllocationStrategy {
	final int startingOffset;
	int currentOffset;
	int minOffset;
	BaseAddress baseAddress;
	List<Integer> bookmarks;
	
	public NegativeAllocationStrategy(BaseAddress baseAddress, int startingOffset) {
		this.startingOffset = startingOffset;
		this.currentOffset = startingOffset;
		this.minOffset = startingOffset;
		this.baseAddress = baseAddress;
		this.bookmarks = new ArrayList<Integer>();
	}
	public NegativeAllocationStrategy(BaseAddress baseAddress) {
		this(baseAddress, 0);
	}

	@Override
	public MemoryLocation allocate(int sizeInBytes) {
		currentOffset -= sizeInBytes;
		updateMin();
		return new MemoryLocation(baseAddress, currentOffset);
	}
	private void updateMin() {
		if(minOffset > currentOffset) {
			minOffset = currentOffset;
		}
	}

	@Override
	public BaseAddress getBaseAddress() {
		return baseAddress;
	}

	@Override
	public int getMaxAllocatedSize() {
		return startingOffset - minOffset;
	}
	
	@Override
	public void saveState() {
		bookmarks.add(currentOffset);
	}
	@Override
	public void restoreState() {
		assert bookmarks.size() > 0;
		int bookmarkIndex = bookmarks.size()-1;
		currentOffset = (int) bookmarks.remove(bookmarkIndex);
	}
}
