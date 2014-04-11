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
																	// didn't
		} // exist

	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		if (byteArray.length > MINIBASE_PAGESIZE) {
			throw new SpaceNotAvailableException(null, "Very large record");
		}
		RID rid = null;
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
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		PageId id = null;
		HFPage temPage = new HFPage();
		return false;
	}

	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException,
			IOException {
		// TODO Auto-generated method stub
		PageId id = null;
		HFPage temPage = new HFPage();
		return false;
	}

	public Tuple getRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		Tuple temp = null;
		return temp;
	}
}
