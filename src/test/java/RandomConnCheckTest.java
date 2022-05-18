import algorithm.Detection;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.apache.commons.net.util.SubnetUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class RandomConnCheckTest extends Detection {
    @Test
    public void mainTest() throws InterruptedException {
        List<String> announcedPrefixes = getAnnouncedPrefixes(133774);
        if (announcedPrefixes.size() == 0) {
            System.out.println("not visible");
            return;
        }

        AtomicBoolean succeed = new AtomicBoolean(false);

        System.out.println("Start checking");
        for (String ip : announcedPrefixes) {
            if (ip.contains(":")) {
                // System.out.println("IPv6 not supported");
                System.out.println("IPv6 now supported!");
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


    @Test
    public void IPv6Test() throws AddressStringException, InterruptedException {
        List<String> announcedPrefixes = getAnnouncedPrefixes(133774);
        if (announcedPrefixes.size() == 0) {
            System.out.println("not visible");
            return;
        }

        AtomicBoolean succeed = new AtomicBoolean(false);
        System.out.println("Start checking");
        // announcedPrefixes = List.of("240e:108:1180::/48");
        for (String ip : announcedPrefixes) {

            IPAddressString ipStr = new IPAddressString(ip);
            IPAddress ipAddr = ipStr.toAddress();


            CountDownLatch latch = new CountDownLatch(1);
            for (IPAddress address : ipAddr.getIterable()) {
                InetAddress inetAddr = address.toInetAddress();
                new Thread(() -> {
                    try {
                        if (inetAddr.isReachable(NetworkInterface.getByName("eth8"),256,500)) {
                            System.out.println("Trying: " + address + " success");
                            succeed.set(true);
                            latch.countDown();
                        } else System.out.println("Trying: " + address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                if (succeed.get())
                    break;
            }
            latch.await();
        }
        System.out.println("Finished");
    }

    @Test
    public void netIfTest() throws SocketException {
    }
}
