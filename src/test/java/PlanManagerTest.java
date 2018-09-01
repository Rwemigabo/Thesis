//
//import static org.junit.Assert.assertEquals;
////import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Arrays;
////import java.util.Collection;
//import java.util.List;
//
////import org.junit.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//
//import com.mycompany.aatr2.DockerManager;
//import com.mycompany.aatr2.Topology;
//import com.mycompany.aatr2.ViableTopologies;
//import com.mycompany.aatr2.analyse.AdaptationRequest;
//import com.mycompany.aatr2.plan.PlanManager;
//import com.spotify.docker.client.exceptions.DockerCertificateException;
//import com.spotify.docker.client.exceptions.DockerException;
//
//@RunWith(Parameterized.class)
//@ExtendWith(AdaptationRequestResolver.class)
//@ExtendWith(TopologyResolver.class)
//class PlanManagerTest {
//	private PlanManager pm;
//	private AdaptationRequest ntop;// topology recommended
//	private Topology expected;
//	private ViableTopologies vt;
//
//	private static Topology vtop2 = new Topology();
//	private static Topology vtop3 = new Topology();
//	private static Topology vtop4 = new Topology();
//	private static Topology vtop5 = new Topology();
//
//	private static String serv1 = "service1";
//	private static String serv2 = "service2";
//	private static String serv3 = "service3";
//	
//	@SuppressWarnings("unused")
//	private DockerManager dm;
//
//	@BeforeEach
//	public void initialize() throws DockerCertificateException, DockerException, InterruptedException {
//		dm = new DockerManager();
//		pm = new PlanManager();
//		vt = ViableTopologies.getInstance();
//		
//		vtop2 = new Topology("top1");
//		vtop3 = new Topology("top2");
//		vtop4 = new Topology("top3");
//		vtop5 = new Topology("top4");
//		
//		vtop2.addService(serv1, 3);
//		vtop2.addService(serv2, 2);
//		vtop2.addService(serv3, 6);
//
//		vtop3.addService(serv1, 6);
//		vtop3.addService(serv2, 7);
//		vtop3.addService(serv3, 5);
//
//		vtop4.addService(serv1, 5);
//		vtop4.addService(serv2, 5);
//		vtop4.addService(serv3, 5);
//
//		vtop5.addService(serv1, 4);
//		vtop5.addService(serv2, 5);
//		vtop5.addService(serv3, 6);
//		vt.addTopology(vtop2);
//		vt.addTopology(vtop3);
//		vt.addTopology(vtop4);
//		vt.addTopology(vtop5);
//	}
//
//	public PlanManagerTest(AdaptationRequest n_top, Topology expected) {
//		this.expected = expected;
//		this.ntop = n_top;// topology recommended
//	}
//
//	@Parameterized.Parameters
//	public static List<Object[]> newTopologies() {
//		AdaptationRequest ar = new AdaptationRequest();
//		AdaptationRequest ar1 = new AdaptationRequest();
//		AdaptationRequest ar2 = new AdaptationRequest();
//
//		ar.addItem(serv1, 5);
//		ar.addItem(serv2, 5);
//		ar.addItem(serv3, 5);
//
//		ar1.addItem(serv1, 3);
//		ar1.addItem(serv2, 5);
//		ar1.addItem(serv3, 9);
//
//		ar2.addItem(serv1, 7);
//		ar2.addItem(serv2, 2);
//		ar2.addItem(serv3, 3);
//
//		return Arrays.asList(new Object[][] { { ar, vtop4}, { ar1, vtop3 }, { ar2, vtop2} });
//	}
//
//	@Test
//	void updateTest() {
//		System.out.println("Parameterized topology : " + ntop);
//		pm.processRequest(ntop);
//		Topology newTopol = pm.getNewT();
//		assertEquals(expected, newTopol.getFilename());
//
//		//fail("Not yet implemented");
//	}
//	
//
//}
