import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {

	public static void main(String args[]) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.out.println("abcd easj   \n ".trim() + "a");
	}

}
