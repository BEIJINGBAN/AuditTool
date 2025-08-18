package Util;

import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class ExcelUtil {

    private static final Logger log = LogManager.getLogger(ExcelUtil.class);

    //分割Excel
    public <T> LinkedHashMap PartitionExcel(List<T> info, int maxSize,String baseName){
        if (info == null || info.size() == 0 ) {
            log.error("没有数据需要分割");
            return null;
        }
        int divisionSize = (info.size() + (maxSize - 1)) / maxSize;

        LinkedHashMap<String,List<T>> InfoMap = new LinkedHashMap<>();
        //文件从1开始
        int ExcelIndex = 1;
        int j = 0;


        while(j < info.size()) {

            List<T> list = new ArrayList<T>();

            int end = Math.min(info.size(),j+maxSize);
            for(; j<end; j++){
                list.add(info.get(j));
            }
            //取名
            String ExcelName = (divisionSize == 1) ? baseName +".xlsx" :  baseName + "_" + String.format("%03d.xlsx",ExcelIndex);

            InfoMap.put(ExcelName,list);

            j = end;

            ExcelIndex++;
        }
        return InfoMap;
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
            try(OutputStream outputStream = new DigestOutputStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
                @Override
                public void write(byte[] b,int off,int len) throws IOException {}
            },messageDigest)){

                final byte[] delimiter = "|".getBytes();

                for (SheetConfig sheet : sheets){
                    outputStream.write(sheet.sheetName.getBytes());
                    outputStream.write(delimiter);

                    for (String[] header : sheet.headerRows){
                        for (String cellValue : header){
                            outputStream.write(cellValue.getBytes());
                            outputStream.write(delimiter);
                        }

                    }
                    for (String[] rowData : sheet.data){
                        for (String cellValue : rowData){
                            if (cellValue != null) {
                                outputStream.write(cellValue.getBytes());
                                outputStream.write(delimiter);
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
    public static String recordIdGenerate(Object obj,String[] ignoreFiellds){
        if (obj == null) {
            return null;
        }
        List<String> ignoreList = Arrays.asList(ignoreFiellds);
        StringBuilder contetBuilder = new StringBuilder();
        try{
            Field[] fields = obj.getClass().getDeclaredFields();
            Arrays.sort(fields,Comparator.comparing(Field::getName));
            for (Field field : fields){
                if (ignoreList.contains(field.getName())){
                    continue;
                }
                contetBuilder.append("|");
            }
        }catch (IllegalArgumentException e){
            log.error("生成唯一ID失败 "+e.getMessage());
        }
        String content = contetBuilder.toString();
        if (content.isEmpty()){
            return null;
        }
        return DigestUtil.sha256Hex(content);
    }
}


