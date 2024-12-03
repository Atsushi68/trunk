package com.taketoritei.order.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class JasperGen {

	@Autowired
	private ApplicationContext appContext;

	/**
	 * BeanをJRDataSourceに変換する
	 *
	 * @param dataSourceBean
	 * @return
	 */
	public JRDataSource createDataSource(List<?> dataSourceList) {

		JRDataSource res = new JRBeanCollectionDataSource(dataSourceList);
		return res;
	}

	/**
	 * jasperReportsを使用してpdfファイルを作成する。
	 *
	 * @param jrxmlFilePath
	 * @param jasperFilePath
	 * @param params
	 * @param dataSource
	 * @param destPath
	 */
	public void outputPdf(JasperPrint basePrint, String outputPdfPath) {

		try {
			// PDFファイルに出力する。
			JasperExportManager.exportReportToPdfFile(basePrint, outputPdfPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public JasperPrint getJasperPrint(String fileName, List<?> list, Map<String, Object> params)
			throws JRException, IOException {

		Resource jasper = appContext.getResource("classpath:report/" + fileName + ".jasper");
		JasperPrint print = null;
		if(!jasper.exists()) {
			Resource jrxml = appContext.getResource("classpath:report/" + fileName + ".jrxml");
			try (InputStream inputStream = jrxml.getInputStream()) {
				JasperReport report =JasperCompileManager.compileReport(inputStream);
				if (list == null) {
					print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
				} else {
					print = JasperFillManager.fillReport(report, params, createDataSource(list));

				}
			}

		} else {
			if (list == null) {
				print = JasperFillManager.fillReport(jasper.getInputStream(), params, new JREmptyDataSource());
			} else {
				print = JasperFillManager.fillReport(jasper.getInputStream(), params, createDataSource(list));
			}
		}

		return print;
	}
}
