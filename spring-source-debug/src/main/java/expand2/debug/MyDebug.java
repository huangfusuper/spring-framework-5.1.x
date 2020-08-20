package expand2.debug;

import expand2.conf.MyConf;
import expand2.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class MyDebug {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MyConf.class);
		UserMapper bean = ac.getBean(UserMapper.class);
		System.out.println(bean.selectId("张三"));

	}


	private static void murgeFile(String folder, String fileName) {

		File destFile = new File(folder, fileName);
		// 使用try-with-resource的方式自动关闭流
		try (FileOutputStream fos = new FileOutputStream(destFile);) {
			int index = 0;
			while (true) {
				DecimalFormat decimalFormat = new DecimalFormat("000000");
				File eachFile = new File(folder, String.format("%s.ts", decimalFormat.format(index++)));
				if (!eachFile.exists()) {
					break;
				}


				// 使用try-with-resource的方式自动关闭流
				try (FileInputStream fis = new FileInputStream(eachFile);) {
					byte[] eachContent = new byte[(int) eachFile.length()];
					fis.read(eachContent);
					fos.write(eachContent);
					fos.flush();
				}
				System.out.printf("把子文件 %s写出到目标文件中%n", eachFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("最后目标文件的大小：%,d字节", destFile.length());

	}
}
