import algorithm.Detection;
import org.apache.commons.net.util.SubnetUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class RandomConnCheckTest extends Detection {
    @Test
    public void mainTest() throws InterruptedException {
        List<String> announcedPrefixes = getAnnouncedPrefixes(23911);
        if (announcedPrefixes.size() == 0) {
            System.out.println("not visible");
            return;
        }

        AtomicBoolean succeed = new AtomicBoolean(false);

        System.out.println("Start checking");
        for (String ip : announcedPrefixes) {
            if (ip.contains(":")) {
                System.out.println("IPv6 no supported");
                continue;
            }
            System.out.println("Trying subnet: " + ip);

            SubnetUtils subnetUtils = new SubnetUtils(ip);
            SubnetUtils.SubnetInfo info = subnetUtils.getInfo();
            if (info.getAddressCountLong() > 60000)
                continue;
            String[] allAddresses = info.getAllAddresses();

            CountDownLatch latch = new CountDownLatch(allAddresses.length);
            for (String s : allAddresses) {
                Thread c = new Thread(() -> {
                    try {
                        InetAddress address = Inet4Address.getByName(s);
                        if (address.isReachable(500)) {
                            System.out.println("Trying: " + s + " success");
                            succeed.set(true);
                        }
                        latch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                c.start();
            }
            if (succeed.get())
                break;
            latch.await();
        }


        System.out.println("Finished");
    }

}
