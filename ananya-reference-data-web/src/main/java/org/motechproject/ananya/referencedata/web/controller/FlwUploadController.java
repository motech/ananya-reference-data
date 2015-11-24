package org.motechproject.ananya.referencedata.web.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.motechproject.ananya.referencedata.csv.ImportType;
import org.motechproject.ananya.referencedata.flw.domain.UploadFlwMetaData;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadFlwMetaData;
import org.motechproject.ananya.referencedata.flw.service.AllUploadCSVRecordsService;
import org.motechproject.ananya.referencedata.web.constants.ProcessConstants;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.importer.model.AllCSVDataImportProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FlwUploadController {
	
	private AllCSVDataImportProcessor allCSVDataImportProcessor;
	private AllUploadFlwMetaData allUploadFlwMetaData;
	private AllUploadCSVRecordsService allUploadCSVRecordsService;
	private static final String PASSED = "passed";
	 
	 @Autowired
	 public FlwUploadController(AllCSVDataImportProcessor allCSVDataImportProcessor,
				AllUploadCSVRecordsService allUploadCSVRecordsService,AllUploadFlwMetaData allUploadFlwMetaData){
		 this.allCSVDataImportProcessor=allCSVDataImportProcessor;
		 this.allUploadCSVRecordsService=allUploadCSVRecordsService;
		 this.allUploadFlwMetaData=allUploadFlwMetaData;
	 }
	 
	 @RequestMapping(method=RequestMethod.POST, value="/admin/flw/upload")
	 private ModelAndView startValidation(CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse, ImportType entity, Boolean shouldUpdateValidRecords, HttpSession session) throws Exception {
		 session.setAttribute("file", csvUploadRequest.getFileData().getFileItem().getName());
		 String uuid = allUploadCSVRecordsService.csvContent(csvUploadRequest.getStringContent());
		 session.setAttribute("uuid", uuid);
		 ModelAndView modelAndView = new ModelAndView("admin/flwUpload");
		 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		 modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		 return modelAndView;
	 }
	 
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/validate")
    private ModelAndView validate(HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
        String result = allCSVDataImportProcessor.get("FrontLineWorker").processValidate(csvUploadRequest);
       
        if(result.equals(PASSED)) {
        	return new ModelAndView("redirect:/admin/flw/validatePassed");
   		}else {
   		    return new ModelAndView("redirect:/admin/flw/validateFailed");
        }

    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/validatePassed")
	private ModelAndView success(HttpSession httpSession) {
		 ModelAndView modelAndView = new ModelAndView("admin/flwValidatePassed");
	     modelAndView.addObject("file",httpSession.getAttribute("file"));
	     modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\" id=\"valid\">Complete</h6>");
		    modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		    modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		    return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/validateFailed")
    private ModelAndView error(HttpSession httpSession) {
    	ModelAndView modelAndView = new ModelAndView("admin/flwValidateFailed");
	     modelAndView.addObject("file",httpSession.getAttribute("file"));
    	 modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"red\" id=\"valid\">Failed</h6>");
	     modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"yellow\">Incomplete</h6>");
		    modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		    return modelAndView;
	}
    
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/persist")
    private ModelAndView persist(HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
		String uuid_persist = allCSVDataImportProcessor.get("FrontLineWorker").processPersist(csvUploadRequest);
		httpSession.setAttribute("uuid-persist", uuid_persist);
        ModelAndView modelAndView = new ModelAndView("admin/flwPersist");
        modelAndView.addObject("file",httpSession.getAttribute("file"));
		modelAndView.addObject(ProcessConstants.VALIDATION, "<h6 class=\"green\">Complete</h6>");
		modelAndView.addObject(ProcessConstants.PERSISTENCE, "<h6 class=\"green\">Complete</h6>");
		modelAndView.addObject(ProcessConstants.SYNC, "<h6 class=\"yellow\">Incomplete</h6>");
		return modelAndView;
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/sync")
    private ModelAndView sync(HttpSession httpSession) throws Exception {
		String uuid_persist = httpSession.getAttribute("uuid-persist").toString();
        String response = (String) allCSVDataImportProcessor.get("FrontLineWorker").processSync(uuid_persist);
        ModelAndView modelAndView = new ModelAndView("admin/flwSync");
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
      		modelAndView.addObject(ProcessConstants.SYNC, " <h6 class=\"red\">Failed</h6>");
      		return modelAndView;
		}
		
        
     }
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/show/meta")
    private ModelAndView show(HttpServletResponse httpServletResponse,HttpSession httpSession) throws Exception {
		String uuidmeta = (String) httpSession.getAttribute("uuid-meta");
		UploadFlwMetaData uploadFlwMetaData = allUploadFlwMetaData.findFlwMetaDataByUUID(uuidmeta);
		ModelAndView modelAndView = new ModelAndView("admin/flwStatus");
		modelAndView.addObject("flwPassed",uploadFlwMetaData.getFlwPassed());
		modelAndView.addObject("flwFailed",uploadFlwMetaData.getFlwFailed());
		modelAndView.addObject("sumflw",uploadFlwMetaData.getFlwFailed()+uploadFlwMetaData.getFlwPassed());
		
		return modelAndView;
    }
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flwError")
	private ModelAndView errorPage(HttpSession httpSession) {
		
	      ModelAndView modelAndView = new ModelAndView("admin/flwError");
	      String fileName = "Flw_upload_failures" + DateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".csv";
	      modelAndView.addObject("downloadErrorCsvName", fileName);
	      return modelAndView;
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/flw/error")
    private void downloadErrorCsv(HttpServletResponse httpServletResponse,HttpSession httpSession) throws Exception {
		String csvUploadRequest = allUploadCSVRecordsService.findCsvFileByUUID(httpSession.getAttribute("uuid").toString()).getContent();
		String errorCsv = allCSVDataImportProcessor.get("FrontLineWorker").download(csvUploadRequest);
        String fileName = "Flw_upload_failures" + DateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".csv";
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(errorCsv.getBytes());
        outputStream.flush();
    }
	
	
	

}
