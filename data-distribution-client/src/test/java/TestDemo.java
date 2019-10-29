/**
 * @author 三多
 * @Time 2019/10/24
 */
public class TestDemo {
    public static void main(String[] args) {
        new ThdTest().start();

        System.out.println(Thread.currentThread().getName());
    }
}

//声明类ThdTest，其父类为Thread类
class ThdTest extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            Thread.currentThread().setName("第【" + i + "】线程");
            try {
                Thread.sleep(100);
                System.out.println(Thread.currentThread().getName() + "\t" + Thread.currentThread().getId());
            } catch (InterruptedException ex) {
                throw new RuntimeException("因为未知原因【" + i + "】线程中断");
            }
        }
    }
}


