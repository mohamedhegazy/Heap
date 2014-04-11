package heap;

import global.PageId;

public class HFPageInfo {
	private short usedPtr;
	private short freeSpace;
	private short type;
	private PageId prevPage = new PageId();
	private PageId nextPage = new PageId();
	private PageId curPage = new PageId();
	private short slotCnt;

	public HFPageInfo(short ptr, short space, short type, short slot,
			PageId prev, PageId next, PageId curId) {
		// TODO Auto-generated constructor stub
		this.usedPtr = ptr;
		this.freeSpace = space;
		this.type = type;
		this.slotCnt = slot;
		this.prevPage.pid = prev.pid;
		this.nextPage.pid = next.pid;
		this.curPage.pid = curId.pid;
	}

	public short getUsedPtr() {
		return usedPtr;
	}

	public void setUsedPtr(short usedPtr) {
		this.usedPtr = usedPtr;
	}

	public short getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(short freeSpace) {
		this.freeSpace = freeSpace;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public PageId getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(PageId prevPage) {
		this.prevPage = prevPage;
	}

	public PageId getNextPage() {
		return nextPage;
	}

	public void setNextPage(PageId nextPage) {
		this.nextPage = nextPage;
	}

	public PageId getCurPage() {
		return curPage;
	}

	public void setCurPage(PageId curPage) {
		this.curPage = curPage;
	}

	public short getSlotCnt() {
		return slotCnt;
	}

	public void setSlotCnt(short slotCnt) {
		this.slotCnt = slotCnt;
	}
}
