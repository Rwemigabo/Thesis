import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import java.util.Random;
import com.mycompany.aatr2.Topology;

public class TopologyResolver implements ParameterResolver {

	@Override
	public Object resolveParameter(ParameterContext arg0, ExtensionContext arg1) throws ParameterResolutionException {
		// TODO Auto-generated method stub
		return new Topology();
	}

	@Override
	public boolean supportsParameter(ParameterContext arg0, ExtensionContext arg1) throws ParameterResolutionException {
		// TODO Auto-generated method stub
		return (arg0.getParameter().getType() == Topology.class);
	}

	int randomNumber(int x, int y) {
		Random r = new Random();
		int Low = x;
		int High = y;
		int Result = r.nextInt(High - Low) + Low;
		return Result;
	}

}