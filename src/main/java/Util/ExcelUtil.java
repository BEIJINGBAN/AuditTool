package Util;

import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class ExcelUtil {

    private static final Logger log = LogManager.getLogger(ExcelUtil.class);

    //分割Excel
    public static <T> LinkedHashMap PartitionExcel(List<T> infos, int maxSize,String baseName){
        if (infos == null || infos.size() == 0 ) {
            log.error("没有数据需要分割");
            return new LinkedHashMap();
        }

        LinkedHashMap<String,List<T>> infoMap = new LinkedHashMap<>();

        List<List<T>> partitions= ListUtils.partition(infos, maxSize);

        int divisionSize = partitions.size();
        int index = 1;

        for (List<T> partition : partitions){

            String excelName = (divisionSize == 1) ? baseName +".xlsx" :  baseName + "_" + String.format("%03d.xlsx",index);//TODO
            infoMap.put(excelName,partition);
            index++;
        }
        return infoMap;
    }
    public static <T> LinkedHashMap PartitionExcel(List<T> infos, int maxSize){
        if (infos == null || infos.size() == 0 ) {
            log.error("没有数据需要分割");
            return new LinkedHashMap();
        }

        LinkedHashMap<String,List<T>> infoMap = new LinkedHashMap<>();

        List<List<T>> partitions= ListUtils.partition(infos, maxSize);

        int divisionSize = partitions.size();
        int index = 1;

        for (List<T> partition : partitions){

            String excelName = "_" + String.format("%03d.xlsx",index);//TODO
            infoMap.put(excelName,partition);
            index++;
        }
        return infoMap;
    }

    //动态创建Excel
    public static class ExcelGenerator {

        //表头信息
        private final List<SheetConfig> sheets = new ArrayList<>();

        //创建动态Excel
        public static ExcelGenerator create() {
            return new ExcelGenerator();
        }

        //增加sheet、标题
        public ExcelGenerator sheet(String sheetName, String[] headers,
                                    List<String[]> data) {
            List<String[]> headerRows = Arrays.asList(new String[][]{headers});
            return sheet(sheetName, headerRows, data);
        }

        //多行
        public ExcelGenerator sheet(String sheetName, List<String[]> headerRows,
                                    List<String[]> data) {
            sheets.add(new SheetConfig(sheetName,headerRows, new ArrayList<>(data)));
            return this;
        }

        public void save(String filePath) throws IOException {
            //创建EXCEL
            XSSFWorkbook workbook = new XSSFWorkbook();


            try {
                for (SheetConfig sheet : sheets){
                    XSSFSheet poiSheet = workbook.createSheet(sheet.sheetName);
                    int rowIndex = 0;

                    //表头
                    for (String[] header : sheet.headerRows){
                        XSSFRow row = poiSheet.createRow(rowIndex++);
                        for (int i = 0; i < header.length; i++) {
                            XSSFCell cell = row.createCell(i);
                            cell.setCellValue(header[i] != null ? header[i] : "");
                            poiSheet.setColumnWidth(i,20 * 256);
                        }
                    }

                    //数据行
                    for (String[] rowData : sheet.data){
                        XSSFRow row = poiSheet.createRow(rowIndex++);
                        for (int i = 0; i < rowData.length; i++){
                            XSSFCell cell = row.createCell(i);
                            cell.setCellValue(rowData[i] != null ? rowData[i] : "");
                        }
                    }
                }
                File file = new File(filePath);
                if (file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (FileOutputStream fos = FileUtils.openOutputStream(file)) {
                    workbook.write(fos);
                }
                log.info("成功生成EXCEL,地址为 "+file.getCanonicalFile());
            }finally {
                workbook.close();
            }
        }
        public String calcultateContentHash() throws IOException, NoSuchAlgorithmException {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try(DigestOutputStream dos = new DigestOutputStream(new ByteArrayOutputStream(), messageDigest)){
                final byte[] delimiter = "|".getBytes(StandardCharsets.UTF_8);
                for (SheetConfig sheet : sheets){
                    dos.write(sheet.sheetName.getBytes(StandardCharsets.UTF_8));
                    dos.write(delimiter);

                    for (String[] header : sheet.headerRows){
                        for (String cellValue : header){
                            dos.write(cellValue.getBytes(StandardCharsets.UTF_8));
                            dos.write(delimiter);
                        }

                    }
                    for (String[] rowData : sheet.data){
                        for (String cellValue : rowData){
                            if (cellValue != null) {
                                dos.write(cellValue.getBytes(StandardCharsets.UTF_8));
                                dos.write(delimiter);
                            }
                        }
                    }
                }
                byte[] hashBytes = messageDigest.digest();
                return DigestUtil.sha256Hex(hashBytes);
            }
        }
    }

    private static class SheetConfig{
        final String sheetName;
        final List<String[]> headerRows;
        final List<String[]> data;

        SheetConfig(String sheetName, List<String[]> headerRows,
                    List<String[]> data) {
            this.sheetName = sheetName;
            this.headerRows = new ArrayList<>(headerRows);
            this.data = data;
        }
    }

    /**
     * 使用模板生成 Excel 文件
     * @param templatePath 模板文件路径
     * @param sheetName 要写入的 sheet 名称
     * @param headers 表头字段名（对应 Java Bean 属性）
     * @param data 数据列表（Java Bean 或 Map）
     * @param startRow 数据开始行（通常为表头下一行，如 1 或 2）
     * @param filePath 输出文件路径
     * @throws IOException
     */
    public static <T> void writeToTemplate(String templatePath,
                                           String sheetName,
                                           String[] headers,
                                           List<T> data,
                                           int startRow,
                                           String filePath) throws IOException {
        File file = new File(templatePath);
        if (!file.exists()) {
            throw new FileNotFoundException("模板文件不存在: " + templatePath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("模板中不存在 sheet: " + sheetName);
            }

            // 从 startRow 开始写数据
            int currentRow = startRow;
            for (T item : data) {
                XSSFRow row = sheet.getRow(currentRow) != null ? sheet.getRow(currentRow) : sheet.createRow(currentRow);
                for (int i = 0; i < headers.length; i++) {
                    XSSFCell cell = row.getCell(i) != null ? row.getCell(i) : row.createCell(i);

                    // 获取字段值（支持 Bean 或 Map）
                    Object value = getFieldValue(item, headers[i]);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    } else {
                        cell.setCellValue("");
                    }
                }
                currentRow++;
            }

            // 删除 startRow 之后多余的空行（可选）
            int lastDataRow = startRow + data.size();
            int lastSheetRow = sheet.getLastRowNum();
            for (int i = lastDataRow; i <= lastSheetRow; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }

            // 创建父目录
            File outFile = new File(filePath);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                workbook.write(fos);
            }

            log.info("成功生成模板Excel: {}", outFile.getCanonicalPath());
        }
    }

    // 反射获取对象字段值（支持 getter 或 public field）
    private static Object getFieldValue(Object obj, String fieldName) {
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).get(fieldName);
        }

        try {
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                // 尝试通过 getter
                String methodName = "get" + capitalize(fieldName);
                java.lang.reflect.Method method = clazz.getMethod(methodName);
                return method.invoke(obj);
            }
        } catch (Exception e) {
            log.warn("无法获取字段值: {}.{}", obj.getClass().getSimpleName(), fieldName, e);
            return null;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}



