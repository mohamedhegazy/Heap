package heap;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

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
	private String name;
	private HFPage head;
	private int reccnt = 0;

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
																	// didn't
																	// exist

			this.name = string;
			head = new HFPage();
			PageId id = SystemDefs.JavabaseBM.newPage(head, 1);
			head.init(id, new Page());
			head.setCurPage(id);
			head.setPrevPage(new PageId(GlobalConst.INVALID_PAGE));
			head.setNextPage(new PageId(GlobalConst.INVALID_PAGE));
			SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);
			SystemDefs.JavabaseDB.add_file_entry(string, id);
		} else {// file exists
			this.name = string;
			head=new HFPage();
			PageId id = SystemDefs.JavabaseDB.get_file_entry(string);
			SystemDefs.JavabaseBM.pinPage(id, head, false);
			head.init(id, head);
			SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);
		}
	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		if (byteArray.length > MINIBASE_PAGESIZE) {
			throw new SpaceNotAvailableException(null, "Very large record");
		}
		RID rid = null;
		PageId id = head.getCurPage();
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id, new Page(temPage.getpage()));
		while (true) {
			rid = temPage.insertRecord(byteArray);
			if (rid != null) {
				SystemDefs.JavabaseBM.unpinPage(id, true);
				reccnt++;
				return rid;
			} else if (rid == null) {
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
			if (temPage.getNextPage().pid == GlobalConst.INVALID_PAGE) {
				HFPage new_page = new HFPage();
				id = SystemDefs.JavabaseBM.newPage(new_page, 1);
				new_page.init(id, new Page());
				temPage.setNextPage(id);
				new_page.setPrevPage(temPage.getCurPage());
				new_page.setCurPage(id);
				new_page.setNextPage(new PageId(GlobalConst.INVALID_PAGE));
				rid = new_page.insertRecord(byteArray);
				SystemDefs.JavabaseBM.unpinPage(new_page.getCurPage(), true);
				reccnt++;
				return rid;

			} else {
				id = temPage.getNextPage();
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);

			}
		}
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
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		PageId id = head.getCurPage();
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id, new Page(temPage.getpage()));
		while (true) {
			boolean record_exists = true;
			try {
				temPage.deleteRecord(rid);
				reccnt--;
				SystemDefs.JavabaseBM.unpinPage(id, true);
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				record_exists = false;
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
			if (!record_exists) {
				id = temPage.getNextPage();
				if (id.pid == GlobalConst.INVALID_PAGE) {
					return false;
				}
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
			}
		}
	}

	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException,
			IOException {
		// TODO Auto-generated method stub
		PageId id = head.getCurPage();
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id, new Page(temPage.getpage()));
		while (true) {
			boolean record_exists = true;
			try {
				Tuple temp = temPage.getRecord(rid);
				if (temp.getLength() != newTuple.getLength()) {
					throw new InvalidUpdateException(null, "Not valid update");
				}

				SystemDefs.JavabaseBM.unpinPage(id, true);
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				record_exists = false;
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
			if (!record_exists) {
				id = temPage.getNextPage();
				if (id.pid == GlobalConst.INVALID_PAGE) {
					return false;
				}
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
			}
		}
	}

	public Tuple getRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		PageId id = head.getCurPage();
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id, new Page(temPage.getpage()));
		while (true) {
			boolean record_exists = true;
			try {
				Tuple temp = temPage.getRecord(rid);
				SystemDefs.JavabaseBM.unpinPage(id, false);
				return temp;
			} catch (Exception e) {
				// TODO: handle exception
				record_exists = false;
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
			if (!record_exists) {
				id = temPage.getNextPage();
				if (id.pid == GlobalConst.INVALID_PAGE) {
					return null;
				}
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
			}
		}
	}

}
