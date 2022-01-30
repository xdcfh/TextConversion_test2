import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversion {

    /**
     * 分隔符
     */
    private static final String SPLIT_CHAR = "\t";

    /**
     * 每次写入excel的缓冲大小
     */
    private static final Integer BUFFER_SIZE = 8192;

    /**
     * 字典文件全路径 - 需要替换成自己的
     */
    private static final String DICTIONARY_TXT_PATH = "C:\\Users\\Administrator\\Desktop\\conversion\\dictionary.txt";

    /**
     * 数据源文件全路径 - 需要替换成自己的
     */
    private static final String EDGE_TXT_PATH = "C:\\Users\\Administrator\\Desktop\\conversion\\edge.txt";

    /**
     * 输出匹配文件的全路径 - 需要替换成自己的
     */
    private static final String EDGE_XLSX_PATH = "C:\\Users\\Administrator\\Desktop\\conversion\\edge.xlsx";

    /**
     * 字典映射map
     */
    private static Map<String, String> dictionaryMap = new HashMap<>();

    /**
     * 初始化
     */
    static {
        try (InputStream inputStream = new FileInputStream(DICTIONARY_TXT_PATH); FileOutputStream outputStream = new FileOutputStream(EDGE_XLSX_PATH)) {
            //1.将dictionary.txt中的映射读入map中
            final BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            String line;
            //逐行读入数据
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    //跳过空行
                    continue;
                }
                //分割文本放入map
                String[] split = line.split(SPLIT_CHAR);
                dictionaryMap.put(split[0], split[1]);
            }
            //2.初始化excel文件
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            //创建sheet页
            xssfWorkbook.createSheet("数据页");
            //写出文件
            xssfWorkbook.write(outputStream);
            //关闭资源
            xssfWorkbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("初始化失败");
        }

    }

    /**
     * 向xlsx文件追加数据
     *
     * @param entries
     */
    public static void appendToXlsx(List<Entry> entries) {
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }

        try (FileInputStream in = new FileInputStream(EDGE_XLSX_PATH)) {
            //读入xlsx数据
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            //获取第一个sheet页信息
            XSSFSheet sheet = workbook.getSheetAt(0);
            //获取sheet页的最后一行行号
            int lastRowNum = sheet.getLastRowNum();
            //循环插入数据
            for (Entry entry : entries) {
                //创建一行数据
                XSSFRow row = sheet.createRow(++lastRowNum);
                //设置该行第一列的数据
                row.createCell(0).setCellValue(entry.getCharOne());
                //设置该行第二列的数据
                row.createCell(1).setCellValue(entry.getCharTwo());
            }
            //输出到文件
            try (FileOutputStream out = new FileOutputStream(EDGE_XLSX_PATH)) {
                //写出数据
                workbook.write(out);
                //关闭资源
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写出excel失败");
        }


    }

    public static void main(String[] args) {
        try (InputStream inputStream = new FileInputStream(EDGE_TXT_PATH)) {
            final BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            //行数据
            String line;
            //缓冲区list
            List<Entry> entries = new ArrayList<>();
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    //跳过空行
                    continue;
                }
                //拆分字符
                String[] split = line.split(SPLIT_CHAR);
                //获取该边第一个数字的映射字符
                String mapOne = dictionaryMap.get(split[0]);
                //获取该边第二个数字的映射字符
                String mapTwo = dictionaryMap.get(split[1]);
                if (isBlank(mapOne) || isBlank(mapTwo)) {
                    //如果两个数字任意一个没有映射上则保持原有边的数字对
                    entries.add(new Entry(split[0], split[1]));
                } else {
                    //全部映射到字符则保存对应字符
                    entries.add(new Entry(mapOne, mapTwo));
                }
                //BUFFER_SIZE 作为一个缓冲大小,达到后会一起写入文件
                if (entries.size() >= BUFFER_SIZE) {
                    appendToXlsx(entries);
                    entries.clear();
                }
            }
            //将缓冲区剩余数据写入文件
            appendToXlsx(entries);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("初始化失败");
        }

    }

    /**
     * 判断是否空字符串
     *
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
