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
			SystemDefs.JavabaseDB.add_file_entry(string, id_head);
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
		id = pages_info.get(pages_info.size() - 1);
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.setCurPage(id);
		HFPage tempHfPage = new HFPage();
		PageId temp = SystemDefs.JavabaseBM.newPage(tempHfPage, 1);
		tempHfPage.init(temp, new Page(tempHfPage.getHFpageArray()));
		pages_info.add(temp);
		tempHfPage.setPrevPage(id);
		temPage.setNextPage(temp);
		rid = tempHfPage.insertRecord(byteArray);
		reccnt++;
		SystemDefs.JavabaseBM.unpinPage(id, true);
		SystemDefs.JavabaseBM.unpinPage(temp, true);
		return rid;
	}

	public HFPage getHead() {
		return head;
	}

	public int getRecCnt() {
		// TODO Auto-generated method stub
		return reccnt;
	}

	public Scan openScan() throws ReplacerException, HashOperationException,
			PageUnpinnedException, InvalidFrameNumberException,
			PageNotReadException, BufferPoolExceededException,
			PagePinnedException, BufMgrException, IOException {
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
		temPage.init(id,new Page(temPage.getHFpageArray()));
		temPage.deleteRecord(rid);
		SystemDefs.JavabaseBM.unpinPage(id, true);
		reccnt--;
		return false;
	}

	@SuppressWarnings("unused")
	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException,
			IOException {
		// TODO Auto-generated method stub
		PageId id = rid.pageNo;
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id,new Page(temPage.getHFpageArray()));
		Tuple temp = temPage.returnRecord(rid);
		if (temp.getLength() != newTuple.getLength()) {
			throw new InvalidUpdateException(null, "Lengths don't match");
		} else if (temp != null) {
			temp.tupleCopy(newTuple);
			SystemDefs.JavabaseBM.unpinPage(id, true);
			return true;
		}
		SystemDefs.JavabaseBM.unpinPage(id, true);
		return false;
	}

	public Tuple getRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, InvalidSlotNumberException {
		// TODO Auto-generated method stub
		PageId id = rid.pageNo;
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id,new Page(temPage.getHFpageArray()));
		Tuple temp = temPage.getRecord(rid);
		SystemDefs.JavabaseBM.unpinPage(id, true);
		return temp;
	}

	public PageId getFirstPageId() {
		// TODO Auto-generated method stub
		return id_head;
	}
}
