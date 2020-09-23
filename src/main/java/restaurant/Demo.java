package restaurant;

import restaurant.employees.nonrunnable.AccountantFactory;
import restaurant.employees.runnable.Chef;
import restaurant.employees.runnable.ChefFactory;
import restaurant.employees.runnable.HostFactory;
import restaurant.employees.runnable.WaiterFactory;
import restaurant.employees.types.RunnableEmployee;
import restaurant.nonrunnable.PatronFactory;
import restaurant.runnable.PatronDistributor;
import restaurant.runnable.Restaurant;
import restaurant.utilities.AtomicIdGen;
import restaurant.utilities.management.HumanResources;
import restaurant.utilities.management.UpperManagement;

public class Demo {

	public static void main(String[] args) throws InterruptedException {
		// Begin by creating the Restaurant.
		String rName = "The Diner";
		Restaurant r = new Restaurant(rName);

		// Then create necessary factories
		ChefFactory cf = new ChefFactory(AtomicIdGen.createDefaultIdGen());
		WaiterFactory wf = new WaiterFactory(AtomicIdGen.createDefaultIdGen());
		AccountantFactory af = new AccountantFactory(AtomicIdGen.createDefaultIdGen());
		HostFactory hf = new HostFactory(AtomicIdGen.createDefaultIdGen());
		
		HumanResources hr = new HumanResources(cf, wf, af, hf, r);
		UpperManagement um = new UpperManagement(hr);
		
		PatronFactory pf = new PatronFactory(AtomicIdGen.createDefaultIdGen());

		// Then create the Patron Distributor, giving it the restaurant
		PatronDistributor pd = new PatronDistributor(pf, r);

		System.out.println("The demo has begun.");

		// Create and start a new thread for the Patron Distributor.
		Thread pThread = new Thread(pd);
		pThread.start();

		// Then create a chef, and promote him to Manager
		Chef c = cf.create(r);
		RunnableEmployee m = um.promoteChefToManager(c);

		// Create, start, and join onto a new thread for the restaurant Manager.
		Thread mThread = new Thread(m);
		mThread.start();
		mThread.join();

		// Because of the join, the current thread will wait until the Manager is
		// finished. Then turn off the Patron Distributor and join onto the Patron
		// Distributor thread.
		pd.setActive(false);
		pThread.join();

		// Because of the join, the current thread will wait until the Patron
		// Distributor is finished.
		System.out.println("The demo has completed.");
	}

}
