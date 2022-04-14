import org.junit.Test;
import process.DetectionResult;

public class ControlPlaneTest {
    @Test
    public void test01() {
        System.out.println(
                new DetectionResult(
                        true,
                        null,
                        null,
                        1, 2, 3)
        );

        System.out.println(
                new DetectionResult()
                        .setResult(false)
                        .setType(0, 1, 3)
        );

        int[] arr = {1, 2, 3, 4};
        int size = arr.length;
        System.out.println(arr[size + (-1)]);
    }
}
