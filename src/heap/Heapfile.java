package heap;

import java.io.IOException;
import java.util.ArrayList;

import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import chainexception.ChainException;
import diskmgr.DiskMgrException;
import diskmgr.DuplicateEntryException;
import diskmgr.FileIOException;
import diskmgr.FileNameTooLongException;
import diskmgr.InvalidPageNumberException;
import diskmgr.InvalidRunSizeException;
import diskmgr.OutOfSpaceException;
import diskmgr.Page;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.SystemDefs;

public class Heapfile implements GlobalConst {
	@SuppressWarnings("unused")
	private String name;
	private HFPage head;
	private PageId id_head;
	private int reccnt = 0;
	private ArrayList<PageId> pages_info;

	public Heapfile(String string) throws IOException,
			BufferPoolExceededException, HashOperationException,
			ReplacerException, HashEntryNotFoundException,
			InvalidFrameNumberException, PagePinnedException,
			PageUnpinnedException, PageNotReadException, BufMgrException,
			DiskMgrException, FileIOException, InvalidPageNumberException,
			FileNameTooLongException, InvalidRunSizeException,
			DuplicateEntryException, OutOfSpaceException {
		// TODO Auto-generated constructor stub
		if (SystemDefs.JavabaseDB.get_file_entry(string) == null) {// heap file
			this.name = string;
			head = new HFPage();
			id_head = SystemDefs.JavabaseBM.newPage(head, 1);
			head.init(id_head, new Page(head.getHFpageArray()));
			SystemDefs.JavabaseBM.unpinPage(id_head, true);
		} else {
			head = new HFPage();
			id_head = SystemDefs.JavabaseDB.get_file_entry(string);
			SystemDefs.JavabaseBM.pinPage(id_head, head, false);
			head.setCurPage(id_head);
			SystemDefs.JavabaseBM.unpinPage(id_head, false);
		}
		pages_info = new ArrayList<PageId>();
		pages_info.add(id_head);

	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		if (byteArray.length > MINIBASE_PAGESIZE) {
			throw new SpaceNotAvailableException(null, "Very large record");
		}
		PageId id = null;
		RID rid = null;
		HFPage temPage = new HFPage();
		for (int i = 0; i < pages_info.size(); i++) {
			id = pages_info.get(i);
			temPage = new HFPage();
			SystemDefs.JavabaseBM.pinPage(id, temPage, false);
			temPage.setCurPage(id);
			rid = temPage.insertRecord(byteArray);
			if (rid != null) {
				SystemDefs.JavabaseBM.unpinPage(id, true);
				reccnt++;
				return rid;
			} else {
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
		}
		temPage = new HFPage();
		id = SystemDefs.JavabaseBM.newPage(temPage, 1);
		temPage.init(id, new Page(temPage.getHFpageArray()));
		temPage.setCurPage(id);
		rid = temPage.insertRecord(byteArray);
		pages_info.add(id);
		reccnt++;
		SystemDefs.JavabaseBM.unpinPage(id, true);
		return rid;
	}

	public HFPage getHead() {
		return head;
	}

	public int getRecCnt() {
		// TODO Auto-generated method stub
		return reccnt;
	}

	public Scan openScan() {
		// TODO Auto-generated method stub
		return new Scan(this);
	}

	public boolean deleteRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, InvalidSlotNumberException {
		// TODO Auto-generated method stub
		PageId id = rid.pageNo;
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.deleteRecord(rid);
		SystemDefs.JavabaseBM.unpinPage(id, true);
		reccnt--;
		return false;
	}

	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException,
			IOException {
		// TODO Auto-generated method stub
		PageId id = rid.pageNo;
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		Tuple temp = temPage.getRecord(rid);
		if (temp.getLength() != newTuple.getLength()) {
			throw new InvalidUpdateException(null, "Lengths don't match");
		} else if (temp != null) {
			temp.tupleCopy(newTuple);
			SystemDefs.JavabaseBM.unpinPage(id, true);
			return true;
		}
		SystemDefs.JavabaseBM.unpinPage(id, false);
		return false;
	}

	public Tuple getRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, InvalidSlotNumberException {
		// TODO Auto-generated method stub
		Tuple temp = null;
		PageId id = null;
		for (int i = 0; i < pages_info.size(); i++) {
			id = pages_info.get(i);
			HFPage temPage = new HFPage();
			SystemDefs.JavabaseBM.pinPage(id, temPage, false);
			temp = temPage.getRecord(rid);
			if (temp != null) {
				SystemDefs.JavabaseBM.unpinPage(id, false);
				return temp;
			}

			SystemDefs.JavabaseBM.unpinPage(id, false);
		}
		return temp;
	}
}
