package packet;

public class GetBookedFlightCountReply extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private int MTL;
	private int WST;
	private int NDL;
	/**
	 * @param mTL
	 * @param wST
	 * @param nDL
	 */
	public GetBookedFlightCountReply(int mTL, int wST, int nDL) {
		super();
		MTL = mTL;
		WST = wST;
		NDL = nDL;
	}
	
	@Override
	public boolean equals(Object o){
		try{
			GetBookedFlightCountReply other = (GetBookedFlightCountReply) o;
			return (this.MTL == other.MTL &&
					this.WST == other.WST &&
					this.NDL == other.NDL)
					?
						true
					:
						false;
		}catch(ClassCastException e){
			return false;
		}
	}

}
