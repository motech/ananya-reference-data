package org.motechproject.ananya.referencedata.web.controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.motechproject.ananya.referencedata.csv.ImportType;
import org.motechproject.ananya.referencedata.flw.domain.UploadLocationMetaData;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadLocationMetaData;
import org.motechproject.ananya.referencedata.flw.service.AllUploadCSVRecordsService;
import org.motechproject.ananya.referencedata.web.constants.ProcessConstants;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.importer.model.AllCSVDataImportProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LocationUploadController {
	

	 private AllCSVDataImportProcessor allCSVDataImportProcessor;
	 private AllUploadLocationMetaData allUploadLocationMetaData;
	 private AllUploadCSVRecordsService allUploadCSVRecordsService;
	 private static final String PASSED = "passed";
		
	 
	@Autowired
	public LocationUploadController(AllCSVDataImportProcessor allCSVDataImportProcessor,
			AllUploadCSVRecordsService allUploadCSVRecordsService,
			AllUploadLocationMetaData allUploadLocationMetaData
			) {
	this.allCSVDataImportProcessor=allCSVDataImportProcessor;
	this.allUploadCSVRecordsService= allUploadCSVRecordsService;
	this.allUploadLocationMetaData = allUploadLocationMetaData;
	 }

	 @RequestMapping(method=RequestMethod.POST, value="/admin/loc/upload")
	 private ModelAndView startValidation(@ModelAttribute("csvUpload") CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse, ImportType entity, Boolean shouldUpdateValidRecords, HttpSession session) throws Exception {
		 session.setAttribute("file", csvUploadRequest.getFileData().getFileItem().getName());
		 String uuid = allUploadCSVRecordsService.csvContent(csvUploadRequest.getStringContent());
		 session.setAttribute("uuid", uuid);
		 ModelAndView modelAndView = new ModelAndView("admin/locUpload");
		 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		 return modelAndView;
	 }
	 
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/validate")
    private ModelAndView validate(HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
		String result = allCSVDataImportProcessor.get("Location").processValidate(csvUploadRequest);
       
        if(result.equals(PASSED)) {
        	return new ModelAndView("redirect:/admin/loc/validatePassed");
        }else {
        	return new ModelAndView("redirect:/admin/loc/validateFailed");
        }

    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/validatePassed")
	private ModelAndView success(HttpSession httpSession) {
		 ModelAndView modelAndView = new ModelAndView("admin/locValidatePassed");
	     modelAndView.addObject("file",httpSession.getAttribute("file"));
	     modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\" id=\"valid\">Complete</h6>");
		 modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		 return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/validateFailed")
    private ModelAndView error(HttpSession httpSession) {
    	 ModelAndView modelAndView = new ModelAndView("admin/locValidateFailed");
	     modelAndView.addObject("file",httpSession.getAttribute("file"));
    	 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"red\" id=\"valid\">Failed</h6>");
	     modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		 return modelAndView;
	}
	
	    
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/persist")
    private ModelAndView persist(HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
		String uuid_persist = allCSVDataImportProcessor.get("Location").processPersist(csvUploadRequest);
		httpSession.setAttribute("uuid-persist", uuid_persist);
        ModelAndView modelAndView = new ModelAndView("admin/locPersist");
        modelAndView.addObject("file",httpSession.getAttribute("file"));
		modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\">Complete</h6>");
		modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"green\">Complete</h6>");
		modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");		
		return modelAndView;
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/sync")
    private ModelAndView sync(HttpSession httpSession) throws Exception {
		 String uuid_persist = httpSession.getAttribute("uuid-persist").toString();
		 String response = (String) allCSVDataImportProcessor.get("Location").processSync(uuid_persist);
         ModelAndView modelAndView = new ModelAndView("admin/locSync");
         modelAndView.addObject("file",httpSession.getAttribute("file"));
         String result = response.split(",")[0];
         String uuidmeta = response.split(",")[1];
         httpSession.setAttribute("uuid-meta", uuidmeta);
         if(result.equals(PASSED)) {
        	 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\">Complete</h6>");
       		modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"green\">Complete</h6>");
       		modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"green\">Complete</h6>");
      		
      		return modelAndView;
         }else {	 
        	 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\">Complete</h6>");
       		modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"green\">Complete</h6>");
       		modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"red\">Failed</h6>");
      		return modelAndView;
		}
		
        
     }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/locError")
	private ModelAndView errorPage(HttpSession httpSession) {
		String fileName = "location_upload_failures" + DateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".csv";
		ModelAndView modelAndView  = new ModelAndView("admin/locError");
		modelAndView.addObject("downloadErrorCsvName", fileName);
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/show/meta")
    private ModelAndView show(HttpSession httpSession) throws Exception {
		String uuidmeta = (String) httpSession.getAttribute("uuid-meta");
		UploadLocationMetaData uploadMetaData = allUploadLocationMetaData.findLocationMetaDataByUUID(uuidmeta);
		ModelAndView modelAndView = new ModelAndView("admin/locStatus");
		modelAndView.addObject("passedValid",uploadMetaData.getPassedValid());
		modelAndView.addObject("passedInvalid",uploadMetaData.getPassedInvalid());
		modelAndView.addObject("failedValid",uploadMetaData.getFailedValid());
		modelAndView.addObject("failedInvalid",uploadMetaData.getPassedInvalid());
		modelAndView.addObject("sumValid",uploadMetaData.getFailedValid()+uploadMetaData.getPassedValid());
		modelAndView.addObject("sumInvalid",uploadMetaData.getFailedInvalid()+uploadMetaData.getPassedInvalid());
		return modelAndView;
    }
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/loc/error")
    private void downloadErrorCsv(HttpServletResponse httpServletResponse,HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
		String errorCsv = allCSVDataImportProcessor.get("Location").download(csvUploadRequest);
        String fileName = "location_upload_failures" + DateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".csv";
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(errorCsv.getBytes());
        outputStream.flush();
    }
	
	
	
}
			

