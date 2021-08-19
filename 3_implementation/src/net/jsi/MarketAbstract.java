package net.jsi;

public abstract class MarketAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	public final static String NONAME = "noname";
	
	
	protected String name = NONAME;
	
	
	public MarketAbstract() {

	}

	
	public MarketAbstract(String name) {
		this.name = name;
	}
	
	
	@Override
	public double getFreeMargin() {
		return getBalance() + getProfit() - getMargin();
	}
	
	
	@Override
	public double getROI() {
		double freeMargin = getFreeMargin();
		double balance = getBalance();
		return (freeMargin - balance) / balance;
	}


	@Override
	public String name() {
		return name;
	}

	
}
