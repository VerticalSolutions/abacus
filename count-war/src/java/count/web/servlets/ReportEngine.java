package count.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 * The
 * <code>ReportEngine</code> class serves html and pdf reports using Jasper
 * reports.
 *
 * @author SkillFusion
 */
public class ReportEngine extends HttpServlet {

    public static String DATE_PATTERN = "yyyyMMdd";
    public static String DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * Called by the server to allow a servlet to handle a GET request.
     *
     * @param request the request the client has made of the servlet
     * @param response the response the servlet sends to the client
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pass this request off to the handleSubmit method. It's an accepted
        // best pracice to handle GET and POST submissions the same way unless
        // there's a good reason not to do so.
        this.handleSubmit(request, response);

    }

    /**
     * Called by the server to allow a servlet to handle a POST request.
     *
     * @param request the request the client has made of the servlet
     * @param response the response the servlet sends to the client
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pass this request off to the handleSubmit method. It's an accepted
        // best pracice to handle GET and POST submissions the same way unless
        // there's a good reason not to do so.
        this.handleSubmit(request, response);
    }

    /**
     * Handles form submissions for
     * <code>#doGet</code> and
     * <code>#doPost</code>.
     *
     * @param request The
     * <code>HttpServletRequest</code> wrapping the HTTP request that triggered
     * this method.
     * @param response The
     * <code>HttpServletResponse</code> that gives this method access to the
     * HTTP response the servlet container will send back to the user agent.
     * @throws IOException when an error occurs while trying to access the
     * output stream to notify the user of an error.
     */
    protected void handleSubmit(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Declare the printwriter (which we'll use if an error occurs), but
        // don't instantiate it yet because instantiating it will prevent us
        // from streaming the PDF back to the client if everything else works.
        PrintWriter out = null;

        Connection conn = null;
        try {
            // Get a JDBC Connection
            Context initContext = new InitialContext();
            DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/countds");
            conn = dataSource.getConnection();
            String reportName = request.getParameter("rpt");
            String output = request.getParameter("output");
            String accesskey = request.getParameter("key");
            Map<String, String> simpleParamsMap = getParameters(request.getParameterMap());
            simpleParamsMap.remove("key");
            simpleParamsMap.remove("output");
            if (accesskey == null || !accesskey.equalsIgnoreCase(getMD5(simpleParamsMap))) {
                throw new IllegalAccessException("Unauthorized!!!");
            }
            HashMap params = setupReportParameters(simpleParamsMap);
            JasperPrint jasperprint = JasperFillManager.fillReport("C:/jasperreports/" + reportName + ".jasper", params,
                    conn);
            if ("odt".equalsIgnoreCase(output)) {
                ODT(response, jasperprint);
            } else if ("docx".equalsIgnoreCase(output)) {
                DOCX(response, jasperprint);
            } else if ("xlsx".equalsIgnoreCase(output)) {
                XLSX(response, jasperprint);
            } else {
                PDF(response, jasperprint);
            }

        } catch (ParseException pe) {
            out = response.getWriter();
            reportException(out, pe, "Failed to parse");
        } catch (IllegalAccessException e) {
            out = response.getWriter();
            reportException(out, e, "Illegal access");
        } catch (NamingException ne) {
            out = response.getWriter();
            String msg = "\t\tDue to a naming problem with this servlet's"
                    + "initial context, the system is unable to display"
                    + "the report at this time.";
            reportException(out, ne, msg);
        } catch (SQLException sqle) {
            out = response.getWriter();
            // Start the html, but don't fill in the juicy bits yet
            out.println("<html>");
            out.println("\t<body>");
            // SQLExceptions can be chained. We have at least one exception, so
            // set up a loop to make sure we let the user know about all of them
            // if there happens to be more than one.
            SQLException tempException = sqle;
            while (null != tempException) {
                // work
                // Write out the useful info on this exception to the response
                out.println("\t\t<br /><br />");
                out.println("\t\tThe following database error occurred:");
                out.println("\t\t<br /><br />");
                out.println("\t\tError Message ==> "
                        + sqle.getLocalizedMessage());
                out.println("\t\t<br />");
                out.println("\t\tCause of Error ==> " + sqle.getCause());
                out.println("\t\t<br />");
                out.println("\t\tSQL State ==> " + sqle.getSQLState());
                out.println("\t\t<br />");
                out.println("\t\tVendor Error Code ==> " + sqle.getErrorCode());
                // loop to the next exception
                tempException = tempException.getNextException();
            }
            out.println("\t</body>");
            out.println("</html>");
        } catch (JRException jre) {
            out = response.getWriter();
            String msg = "\t\tJasper encountered a problem when attempting"
                    + "to populate the report.";
            reportException(out, jre, msg);
        } catch (IOException ioe) {
            out = response.getWriter();
            String msg = "\t\tDue to a naming problem with this servlet's"
                    + "initial context, the system is unable to display"
                    + "the report at this time.";
            reportException(out, ioe, msg);
        } finally {
            try {
                // We're done here, so clean up the connection
                conn.close();
            } catch (Exception e) {
                // We don't actually care, we were just trying to clean up an
                // expensive DB connection
            }
        }
    }
    public class IllegalAccessException extends Exception{
        public IllegalAccessException(String msg){
            super(msg);
        }
    }

    public static String getMD5(Map<String, String> map) {
        Map<String, String> treeMap = new TreeMap<String, String>(map);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry entry : treeMap.entrySet()) {
            builder.append(entry.getValue());
        }
        return getMD5(builder.toString().getBytes());
    }

    public static String getMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] array = md.digest(input);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap setupReportParameters(Map<String, String> map) throws NumberFormatException, ParseException {
        HashMap params = new HashMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.contains("_dateTime")) {
                DateFormat format = new SimpleDateFormat(DATE_PATTERN);
                Date result = format.parse(value);
                params.put(key, result);
            } else if (key.contains("_date")) {
                DateFormat format = new SimpleDateFormat(DATE_PATTERN);
                Date result = format.parse(value);
                params.put(key, result);
            } else if (key.contains("_int")) {
                int result = Integer.parseInt(value);
                params.put(key, result);
            } else {
                params.put(key, value);
            }
        }
        return params;
    }

    private HashMap<String, String> getParameters(Map<String, String[]> map) throws NumberFormatException, ParseException {
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue()[0]);
        }
        return result;
    }

    private void reportException(PrintWriter out, Exception e, String customMessage) {
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<br /><br />");
        out.println(customMessage);
        out.println("\t\t<br /><br />");
        if (e != null) {
            out.println("\t\tError Message ==> " + e.getLocalizedMessage());
            out.println("\t\t<br />");
            out.println("\t\tCause of Error ==> " + e.getCause());
        }
        out.println("\t</body>");
        out.println("</html>");
    }

    public void PDF(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        response.setContentType("application/pdf");
        response.addHeader("Content-disposition", "inline; filename=report.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

    }

    public void DOCX(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        response.addHeader("Content-disposition", "inline; filename=report.docx");
        JRAbstractExporter docxExporter = new JRDocxExporter();
        exportReport(docxExporter, jasperPrint, response.getOutputStream());
    }

    public void XLSX(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        response.addHeader("Content-disposition", "attachment; filename=report.xlsx");
        JRAbstractExporter docxExporter = new JRXlsxExporter();
        exportReport(docxExporter, jasperPrint, response.getOutputStream());
    }

    public void ODT(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        response.addHeader("Content-disposition", "attachment; filename=report.odt");
        JRAbstractExporter docxExporter = new JROdtExporter();
        exportReport(docxExporter, jasperPrint, response.getOutputStream());
    }

    public void PPT(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        response.addHeader("Content-disposition", "attachment; filename=report.pptx");
        JRAbstractExporter docxExporter = new JRPptxExporter();
        exportReport(docxExporter, jasperPrint, response.getOutputStream());
    }

    private void exportReport(JRAbstractExporter docxExporter, JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();
    }
}