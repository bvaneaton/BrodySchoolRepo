package symbolTable;

import java.util.ArrayList;
import java.util.List;

public class PositiveAllocationStrategy implements AllocationStrategy {
	final int startingOffset;	
	int currentOffset;
	int maxOffset;
	BaseAddress baseAddress;
	List<Integer> bookmarks;
	
	public PositiveAllocationStrategy(BaseAddress baseAddress, int startingOffset) {
		this.startingOffset = startingOffset;
		this.currentOffset = startingOffset;
		this.maxOffset = startingOffset;
		this.baseAddress = baseAddress;
		this.bookmarks = new ArrayList<Integer>();
	}
	public PositiveAllocationStrategy(BaseAddress baseAddress) {
		this(baseAddress, 0);
	}

	@Override
	public MemoryLocation allocate(int sizeInBytes) {
		int offset = currentOffset;
		currentOffset += sizeInBytes;
		updateMax();
		return new MemoryLocation(baseAddress, offset);
	}
	private void updateMax() {
		if(maxOffset < currentOffset) {
			maxOffset = currentOffset;
		}
	}
	@Override
	public BaseAddress getBaseAddress() {
		return baseAddress;
	}
	@Override
	public int getMaxAllocatedSize() {
		return maxOffset - startingOffset;
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
