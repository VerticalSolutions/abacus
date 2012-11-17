/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

/**
 *
 * @author Rommel
 */
public class DataExportBean {

    /**
     * Creates a new instance of DataExportBean
     */
    public DataExportBean() {
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);

            cell.setCellStyle(cellStyle);
        }
    }

//    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
//        Document pdf = (Document) document;
//        pdf.open();
//        pdf.setPageSize(PageSize.A4);
//
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "prime_logo.png";
//
//        pdf.add(Image.getInstance(logo));
//    }
}
