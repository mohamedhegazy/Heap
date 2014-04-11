package heap;

import diskmgr.Page;
import global.Convert;
import global.GlobalConst;
import global.PageId;
import global.RID;
import java.io.IOException;

public class HFPage extends Page implements ConstSlot, GlobalConst {
	public static final int SIZE_OF_SLOT = 4;
	public static final int DPFIXED = 20;
	public static final int SLOT_CNT = 0;
	public static final int USED_PTR = 2;
	public static final int FREE_SPACE = 4;
	public static final int TYPE = 6;
	public static final int PREV_PAGE = 8;
	public static final int NEXT_PAGE = 12;
	public static final int CUR_PAGE = 16;

	public short getUsedPtr() {
		return usedPtr;
	}

	public void setUsedPtr(short usedPtr) throws IOException {
		this.usedPtr = usedPtr;
		Convert.setShortValue(this.usedPtr, 2, this.data);
	}

	public short getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(short freeSpace) throws IOException {
		this.freeSpace = freeSpace;
		Convert.setShortValue(this.freeSpace, 4, this.data);
	}

	public void setSlotCnt(short slotCnt) throws IOException {
		this.slotCnt = slotCnt;
		Convert.setShortValue(this.slotCnt, 0, this.data);
	}

	private short slotCnt;
	private short usedPtr;
	private short freeSpace;
	private short type;
	private PageId prevPage = new PageId();
	private PageId nextPage = new PageId();
	protected PageId curPage = new PageId();

	public HFPage() {
	}

	public HFPage(Page paramPage) {
		this.data = paramPage.getpage();
	}

	public void openHFpage(Page paramPage) {
		this.data = paramPage.getpage();
	}

	public void init(PageId paramPageId, Page paramPage) throws IOException {
		this.data = paramPage.getpage();

		this.slotCnt = 0;
		Convert.setShortValue(this.slotCnt, 0, this.data);

		this.curPage.pid = paramPageId.pid;
		Convert.setIntValue(this.curPage.pid, 16, this.data);

		this.nextPage.pid = (this.prevPage.pid = -1);
		Convert.setIntValue(this.prevPage.pid, 8, this.data);
		Convert.setIntValue(this.nextPage.pid, 12, this.data);

		this.usedPtr = 1024;
		Convert.setShortValue(this.usedPtr, 2, this.data);

		this.freeSpace = 1004;
		Convert.setShortValue(this.freeSpace, 4, this.data);
	}

	public byte[] getHFpageArray() {
		return this.data;
	}

	public void dumpPage() throws IOException {
		this.curPage.pid = Convert.getIntValue(16, this.data);
		this.nextPage.pid = Convert.getIntValue(12, this.data);
		this.usedPtr = Convert.getShortValue(2, this.data);
		this.freeSpace = Convert.getShortValue(4, this.data);
		this.slotCnt = Convert.getShortValue(0, this.data);

		System.out.println("dumpPage");
		System.out.println("curPage= " + this.curPage.pid);
		System.out.println("nextPage= " + this.nextPage.pid);
		System.out.println("usedPtr= " + this.usedPtr);
		System.out.println("freeSpace= " + this.freeSpace);
		System.out.println("slotCnt= " + this.slotCnt);

		int i = 0;
		for (int j = 20; i < this.slotCnt; i++) {
			int k = Convert.getShortValue(j, this.data);
			int m = Convert.getShortValue(j + 2, this.data);
			System.out.println("slotNo " + i + " offset= " + m);
			System.out.println("slotNo " + i + " length= " + k);
			j += 4;
		}
	}

	public PageId getPrevPage() throws IOException {
		this.prevPage.pid = Convert.getIntValue(8, this.data);
		return this.prevPage;
	}

	public void setPrevPage(PageId paramPageId) throws IOException {
		this.prevPage.pid = paramPageId.pid;
		Convert.setIntValue(this.prevPage.pid, 8, this.data);
	}

	public PageId getNextPage() throws IOException {
		this.nextPage.pid = Convert.getIntValue(12, this.data);
		return this.nextPage;
	}

	public void setNextPage(PageId paramPageId) throws IOException {
		this.nextPage.pid = paramPageId.pid;
		Convert.setIntValue(this.nextPage.pid, 12, this.data);
	}

	public PageId getCurPage() throws IOException {
		this.curPage.pid = Convert.getIntValue(16, this.data);
		return this.curPage;
	}

	public void setCurPage(PageId paramPageId) throws IOException {
		this.curPage.pid = paramPageId.pid;
		Convert.setIntValue(this.curPage.pid, 16, this.data);
	}

	public short getType() throws IOException {
		this.type = Convert.getShortValue(6, this.data);
		return this.type;
	}

	public void setType(short paramShort) throws IOException {
		this.type = paramShort;
		Convert.setShortValue(this.type, 6, this.data);
	}

	public short getSlotCnt() throws IOException {
		this.slotCnt = Convert.getShortValue(0, this.data);
		return this.slotCnt;
	}

	public void setSlot(int paramInt1, int paramInt2, int paramInt3)
			throws IOException {
		int i = 20 + paramInt1 * 4;
		Convert.setShortValue((short) paramInt2, i, this.data);
		Convert.setShortValue((short) paramInt3, i + 2, this.data);
	}

	public short getSlotLength(int paramInt) throws IOException {
		int i = 20 + paramInt * 4;
		short s = Convert.getShortValue(i, this.data);
		return s;
	}

	public short getSlotOffset(int paramInt) throws IOException {
		int i = 20 + paramInt * 4;
		short s = Convert.getShortValue(i + 2, this.data);
		return s;
	}

	public RID insertRecord(byte[] paramArrayOfByte) throws IOException {
		RID localRID = new RID();
		int i = paramArrayOfByte.length;
		int j = i + 4;
		this.freeSpace = Convert.getShortValue(4, this.data);
		if (j > this.freeSpace) {
			return null;
		}
		this.slotCnt = Convert.getShortValue(0, this.data);
		int k ;
		for ( k = 0; k < this.slotCnt; k++) {
			int m = getSlotLength(k);
			if (m == -1) {
				break;
			}
		}
		if (k == this.slotCnt) {
			this.freeSpace = ((short) (this.freeSpace - j));
			Convert.setShortValue(this.freeSpace, 4, this.data);

			this.slotCnt = ((short) (this.slotCnt + 1));
			Convert.setShortValue(this.slotCnt, 0, this.data);
		} else {
			this.freeSpace = ((short) (this.freeSpace - i));
			Convert.setShortValue(this.freeSpace, 4, this.data);
		}
		this.usedPtr = Convert.getShortValue(2, this.data);
		this.usedPtr = ((short) (this.usedPtr - i));
		Convert.setShortValue(this.usedPtr, 2, this.data);
		setSlot(k, i, this.usedPtr);
		System.arraycopy(paramArrayOfByte, 0, this.data, this.usedPtr, i);
		this.curPage.pid = Convert.getIntValue(16, this.data);
		localRID.pageNo.pid = this.curPage.pid;
		localRID.slotNo = k;
		return localRID;
	}

	public void deleteRecord(RID paramRID) throws IOException,
			InvalidSlotNumberException {
		int i = paramRID.slotNo;
		int j = getSlotLength(i);
		this.slotCnt = Convert.getShortValue(0, this.data);
		if ((i >= 0) && (i < this.slotCnt) && (j > 0)) {
			int k = getSlotOffset(i);
			this.usedPtr = Convert.getShortValue(2, this.data);
			int m = this.usedPtr + j;
			int n = k - this.usedPtr;

			System.arraycopy(this.data, this.usedPtr, this.data, m, n);

			int i1 = 0;
			for (int i2 = 20; i1 < this.slotCnt; i1++) {
				if (getSlotLength(i1) >= 0) {
					int i3 = getSlotOffset(i1);
					if (i3 < k) {
						i3 += j;
						Convert.setShortValue((short) i3, i2 + 2, this.data);
					}
				}
				i2 += 4;
			}
			this.usedPtr = ((short) (this.usedPtr + j));
			Convert.setShortValue(this.usedPtr, 2, this.data);

			this.freeSpace = Convert.getShortValue(4, this.data);
			this.freeSpace = ((short) (this.freeSpace + j));
			Convert.setShortValue(this.freeSpace, 4, this.data);

			setSlot(i, -1, 0);
		} else {
			throw new InvalidSlotNumberException(null,
					"HEAPFILE: INVALID_SLOTNO");
		}
	}

	public RID firstRecord() throws IOException {
		RID localRID = new RID();
		int i = 0;

		this.slotCnt = Convert.getShortValue(0, this.data);
		for (i = 0; i < this.slotCnt; i++) {
			int j = getSlotLength(i);
			if (j != -1) {
				break;
			}
		}
		if (i == this.slotCnt) {
			return null;
		}
		localRID.slotNo = i;
		this.curPage.pid = Convert.getIntValue(16, this.data);
		localRID.pageNo.pid = this.curPage.pid;

		return localRID;
	}

	public RID nextRecord(RID paramRID) throws IOException {
		RID localRID = new RID();
		this.slotCnt = Convert.getShortValue(0, this.data);

		int i = paramRID.slotNo;
		for (i++; i < this.slotCnt; i++) {
			int j = getSlotLength(i);
			if (j != -1) {
				break;
			}
		}
		if (i >= this.slotCnt) {
			return null;
		}
		localRID.slotNo = i;
		this.curPage.pid = Convert.getIntValue(16, this.data);
		localRID.pageNo.pid = this.curPage.pid;

		return localRID;
	}

	public Tuple getRecord(RID paramRID) throws IOException,
			InvalidSlotNumberException {
		PageId localPageId = new PageId();
		localPageId.pid = paramRID.pageNo.pid;
		this.curPage.pid = Convert.getIntValue(16, this.data);
		int k = paramRID.slotNo;

		int i = getSlotLength(k);
		this.slotCnt = Convert.getShortValue(0, this.data);
		if ((k >= 0) && (k < this.slotCnt) && (i > 0)
				&& (localPageId.pid == this.curPage.pid)) {
			int j = getSlotOffset(k);
			byte[] arrayOfByte = new byte[i];
			System.arraycopy(this.data, j, arrayOfByte, 0, i);
			Tuple localTuple = new Tuple(arrayOfByte, 0, i);
			return localTuple;
		}
		throw new InvalidSlotNumberException(null, "HEAPFILE: INVALID_SLOTNO");
	}

	public Tuple returnRecord(RID paramRID) throws IOException,
			InvalidSlotNumberException {
		PageId localPageId = new PageId();
		localPageId.pid = paramRID.pageNo.pid;

		this.curPage.pid = Convert.getIntValue(16, this.data);
		int k = paramRID.slotNo;

		int i = getSlotLength(k);
		this.slotCnt = Convert.getShortValue(0, this.data);
		if ((k >= 0) && (k < this.slotCnt) && (i > 0)
				&& (localPageId.pid == this.curPage.pid)) {
			int j = getSlotOffset(k);
			Tuple localTuple = new Tuple(this.data, j, i);
			return localTuple;
		}
		throw new InvalidSlotNumberException(null, "HEAPFILE: INVALID_SLOTNO");
	}

	public int available_space() throws IOException {
		this.freeSpace = Convert.getShortValue(4, this.data);
		return this.freeSpace - 4;
	}

	public boolean empty() throws IOException {
		this.slotCnt = Convert.getShortValue(0, this.data);
		for (int i = 0; i < this.slotCnt; i++) {
			int j = getSlotLength(i);
			if (j != -1) {
				return false;
			}
		}
		return true;
	}

	protected void compact_slot_dir() throws IOException {
		int i = 0;
		int j = -1;
		int k = 0;

		this.slotCnt = Convert.getShortValue(0, this.data);
		this.freeSpace = Convert.getShortValue(4, this.data);
		while (i < this.slotCnt) {
			int m = getSlotLength(i);
			if ((m == -1) && (k == 0)) {
				k = 1;
				j = i;
			} else if ((m != -1) && (k == 1)) {
				int n = getSlotOffset(i);

				setSlot(j, m, n);

				setSlot(i, -1, 0);

				j++;
				while (getSlotLength(j) != -1) {
					j++;
				}
			}
			i++;
		}
		if (k == 1) {
			this.freeSpace = ((short) (this.freeSpace + 4 * (this.slotCnt - j)));
			this.slotCnt = ((short) j);
			Convert.setShortValue(this.freeSpace, 4, this.data);
			Convert.setShortValue(this.slotCnt, 0, this.data);
		}
	}
}
