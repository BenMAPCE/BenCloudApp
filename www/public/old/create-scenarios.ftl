<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>BenMAP Global Online - Create and Run Scenarios</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="/css/ztree_v3/zTreeStyle/zTreeStyle.css">
    <link rel="stylesheet" href="/css/ztree_v3/demo.css">
    <link rel="stylesheet" href="/css/leaflet/leaflet.css">
    <link rel="stylesheet" href="/css/leaflet/leaflet.zoomhome.css">
    <link rel="stylesheet" href="/css/fontawesome/all.min.css">
    <link rel="stylesheet" href="/css/custom.css">

    <script src="/js/jquery/jquery.min.js"></script>
    <script src="/js/js-cookie.js"></script>
   	<script src="/js/popper/umd/popper.min.js"></script>
    <script src="/js/bootstrap/bootstrap.min.js"></script>
    <script src="/js/ztree_v3/jquery.ztree.core.js"></script>
    <script src="/js/ztree_v3/jquery.ztree.excheck.js"></script>
    <script src="/js/leaflet/leaflet.js"></script>
    <script src="/js/leaflet/leaflet.zoomhome.js"></script>
    <script src="/js/fontawesome/fontawesome.min.js"></script>
    <script src="/js/scenarios.js"></script>
    <script src="/js/regions-countries-tree.js"></script>
    <script src="/js/map.js"></script>
    <script src="/js/forms.js"></script>
    <script src="/js/country-population.js"></script>

    <script type="text/javascript">
        
    var regionsTree
    var countriesTree

	var regionCountries = JSON.parse('${regions4tree}');
	var countries = JSON.parse('${countries4tree}');
	
    var countriesSelected = "";

        $(document).ready(function() {
            $("#scenario-card").css('display', 'block')
            createTree();
  
            map = L.map('map', {
                center: [43.733334, 7.416667],
                zoom: 1,
                zoomControl: false
            });
            
            var zoomHome = L.Control.zoomHome();
			zoomHome.addTo(map);
          
	        $("#regions-list").click(function() {
	            $("#regions-list").prop('checked', true);
	            $("#countries-list").prop('checked', false);
	            $("#countriesTree").hide()
	            $("#regionsTree").show()

	            //$.fn.zTree.destroy("treeDemo");
	            //regionsTree = $.fn.zTree.init($("#treeDemo"), setting, regionCountries);
	            checkNodesInCountriesSelected(regionsTree)
	        });

	        $("#countries-list").click(function() {
	            $("#regions-list").prop('checked', false);
	            $("#countries-list").prop('checked', true);
	            $("#countriesTree").show()
	            $("#regionsTree").hide()
	            //$.fn.zTree.destroy("treeDemo");
	            //countriesTree = $.fn.zTree.init($("#treeDemo"), setting, countries);
	            checkNodesInCountriesSelected(countriesTree)
	        });

	        $('#vsl-standard').on('change', function() {
	            vslStandardTypeSelected = $(this).find(":selected").val();
	            switch(vslStandardTypeSelected) {
	                case "1":
	                    $("#vsl-1-table").show();
	                    $("#vsl-2-table").hide();
	                    break;
	            
	                case "2":
	                    $("#vsl-1-table").hide();
	                    $("#vsl-2-table").show();
	                    break;
	            }
	        });

			$('#rollback-card-form-error').hide();
			
	        $('#rollback-type').on('change', function() {
	        	$('#rollback-card-form-error').html("");
				$('#rollback-card-form-error').hide();

	            rollbackTypeSelected = $(this).find(":selected").val();
	            switch(rollbackTypeSelected) {
	                case "percentage":
	                    $("#percentage-options").show();
	                    $("#incremental-options").hide();
	                    $("#standard-options").hide();
						jQuery("#aq-rollback-image").attr("src", "/images/GBD_Percentage.png")
	                    break;
	            
	                case "incremental":
	                    $("#percentage-options").hide();
	                    $("#incremental-options").show();
	                    $("#standard-options").hide();
						jQuery("#aq-rollback-image").attr("src", "/images/GBD_Increment.png")
	                    break;

	                case "standard":
	                    $("#percentage-options").hide();
	                    $("#incremental-options").hide();
	                    $("#standard-options").show();
						jQuery("#aq-rollback-image").attr("src", "/images/GBD_Standard.png")
	                    break;
	            }
	            
	        });

        });
        
        $(function(){
            //console.log("here");
            var includes = $('[data-include]');
            //console.log(includes);
            $.each(includes, function(){
                var file = $(this).data('include') + '.html';
                //console.log(file)
                $(this).load(file);
            });
        });

    </script>

</head>

<body>
	<#include 'navigation.ftl'>

    <div class="container-fluid" style="margin: 25px 25px 25px 25px;">

        <div class="row">
            <div class="col-4" style="border: 0px solid black;" id="left-panel">

                <div id="scenario-card" class="card">
                    <div class="card-header">
                        Create a new scenario
                    </div>
                    <div class="card-body">
                        <form>
                            <div class="form-group">
                                <label for="scenarioName">Scenario Name</label>
                                <input type="text" class="form-control" id="scenarioName" aria-describedby="emailHelp" placeholder="">
                            </div>
                            
                            <div id="scenario-card-form-error"></div>

                            <div class="form-group">
                                <label for="scenarioDescription">Scenario Description</label>
                                <textarea class="form-control" id="scenarioDescription" rows="4"></textarea>
                            </div>

                            <div class="button-row">
                                <div>
                                    <button type="submit" id="select-region-button" onclick="showSelectRegionsCard();return false;" class="btn btn-primary float-right">Region Selection</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div id="region-selections-card" class="card">
                    <div class="card-header">
                        Region Selection
                    </div>
                    <div class="card-body">

						<div id="regions-card-form-error"></div>

                        <div class="custom-control custom-radio custom-control-inline">
                            <input type="radio" class="custom-control-input" id="regions-list" name="treeType" checked>
                            <label class="custom-control-label" for="regions-list">Regions</label>
                        </div>

                        <div class="custom-control custom-radio custom-control-inline">
                            <input type="radio" class="custom-control-input" id="countries-list" name="treeType">
                            <label class="custom-control-label" for="countries-list">Countries</label>
                        </div>

                        <div class="zTreeDemoBackground left">
                            <ul id="regionsTree" class="ztree"></ul>
                            <ul id="countriesTree" class="ztree"></ul>
                        </div>

                        <div class="button-row">
                            <button type="submit" id="select-scenario" onclick="showScenarioCard();return false;" class="btn btn-secondary">Select Scenario</button>
                            <button type="submit" id="select-rollback-settings" onclick="showRollbackSettingsCard();return false;" class="btn btn-primary float-right">Rollback Settings</button>
                        </div>
                    </div>
                </div>


                <div id="rollback-settings-card" class="card">
                    <div class="card-header">
                        Rollback Settings
                    </div>
                    <div class="card-body">
                        <form id="rollback-card-form">
                            <div class="form-group">


                               <label for="rollbackType">Rollback Type</label>
                               <select id="rollback-type" class="custom-select">
                                    <option value="percentage">Percentage Rollback</option>
                                    <option value="incremental">Incremental Rollback</option>
                                    <option value="standard">Rollback to a Standard</option>
                                </select>

                                <div id="percentage-options">
                                    <div class="form-group">
                                        <label for="percentage">Percentage </label>
                                        <input type="text" class="form-control" id="percentageValue" aria-describedby="percentageHelp" placeholder="">
                                    </div>
                                </div>
                                <div id="incremental-options">
                                    <div class="form-group">
                                        <label for="increment">Increment (ug/m^3) </label>
                                        <input type="text" class="form-control" id="incrementValue" aria-describedby="incrementHelp" placeholder="">
                                    </div>
                                </div>

                                <div id="standard-options">
                                
                                	<label for="standard">Standard</label>
										<select class="custom-select" id="standards-list">
											<#list standards as standardId, standardName>
												<option value="${standardId}">${standardName}</option>
											</#list>
                                		</select>
                                    <br/>
                                    <div class="custom-control custom-checkbox negative-rollback">
                                        <input type="checkbox" class="custom-control-input" id="negativeRollback">
                                        <label class="custom-control-label" for="negativeRollback">Negative Rollback to a Standard</label>
                                    </div>
                                </div>

                            </div>
							<div id="rollback-card-form-error"></div>

							<div>
								<img src="/images/GBD_Percentage.png" id="aq-rollback-image" class="aq-rollback-image"/>
							</div>
							
							<div>
                            <label for="rollbackType">Function</label>
                               <select class="custom-select" id="function">
                                    <option value="2">Burnett et al., 2017 - SCHIF</option>
                                    <option value="3">Burnett et al., 2014 - IER</option>
                                    <option value="1">Krewski et al., 2009</option>
                                </select>
							</div>
							
                            <div class="button-row">
                                <button type="submit" id="select-region-button" onclick="showSelectRegionsCard();return false;" class="btn btn-secondary">Region Selection</button>
                                <button type="submit" id="select-mortality-button" onclick="showMortalityValuationCard();return false;" class="btn btn-primary float-right">Mortality Valuation</button>
                            </div>
                        </form>
                    </div>
                </div>

                <div id="mortality-valuation-card" class="card">
                    <div class="card-header">
                        Mortality Valuation
                    </div>
                    <div class="card-body">
                        <form>
                                <label for="vslType">VSL Standard</label>
                               <select id="vsl-standard"class="custom-select">
                                    <option value="1">OECD VSL</option>
                                    <option value="2">USEPA VSL</option>
                                </select>
                                
                                <div style="margin: 20px 40px;">
                                    <div id="vsl-1-table">
                                    
                                    
                                    	<div class="table-wrapper-scroll-y my-custom-scrollbar">
											<table class="table table-bordered table-striped mb-0 vsl-table">
    											<thead>
        											<tr><th>COUNTRY</th><th>VSL (USD PPP)</th></tr>
   												 </thead>
   												 <tbody>
    												<#list vsl1 as vslCountry, vslValue>
 												 		<tr><td>${vslCountry}</td><td>${vslValue?round}</td></tr>
 												 	</#list>
   												 </tbody>
											</table>
										</div>
                                    </div>
                                    
                                    <div id="vsl-2-table">
                                    
                                    	<div class="table-wrapper-scroll-y my-custom-scrollbar">
											<table class="table table-bordered table-striped mb-0 vsl-table">
    											<thead>
        											<tr><th>COUNTRY</th><th>VSL (USD PPP)</th></tr>
    											</thead>
    											<tbody>
    												<#list vsl2 as vslCountry, vslValue>
 												 		<tr><td>${vslCountry}</td><td>${vslValue?round}</td></tr>
 												 	</#list>
												</tbody>
											</table>
										</div>
                                    </div>
                                </div>

                            <div class="button-row">
                                <button type="submit" id="select-region-button" onclick="showRollbackSettingsCard();return false;" class="btn btn-secondary">Rollback Settings</button>
                                <button type="submit" id="select-mortality-button" onclick="addToScenarios();return false;" class="btn btn-primary float-right">Save Scenario</button>
                            </div>
                        </form>
                    </div>
                </div>

            </div>

            <div class="col-6" id="map" style="border: 1px solid black;" id="left-panel">
            </div>

        </div>
        <div class="row" style="padding-top: 15px;">
            <div id="scenarios-table-panel" class="col-10" style="border: 0px solid black;">
            
  				<div>
   					<div aria-label="Scenarios" style="float: left">
        				<button type="button" class="btn btn-outline-secondary btn-sm" id="submit-selected">Submit Selected</button>
        				<label class="mdb-main-label">in Format </label>
        				<select id="output-type" class="custom-select-sm">
         	   				<option value="XLS">Excel</option>
            				<option value="CSV">CSV</option>
        				</select>
   					</div>

    				<div class="table-wrapper-scroll-y my-custom-scrollbar" style="clear: both; float: left; padding-top: 20px;">
        				<table class="table table-bordered table-striped mb-0" id="scenario-table">
            				<thead>
                				<tr>
              	    	  			<th></th>
                    				<th>Scenario Name</th>
                    				<th>Total Countries</th>
                    				<th>Total Population</th>
                    				<th>Type of Rollback</th>
                    				<th>Function</th>
                    				<th>VSL Standard</th>
                    				<th width="130px">Actions</th>
                				</tr>
            				</thead>
            				<tbody>
            				</tbody>
        				</table>
    				</div>
				</div>          
              </div>
        </div>
    </div>

<div class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Scenario Submissions</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Modal body text goes here.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary">Save changes</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

  <!-- Modal -->
	<div class="modal fade" id="scenariosSubmittedListModal" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-body">
					<div id="scenariosSubmitted"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary btn-sm" data-dismiss="modal">Close</button>
				</div>      
			</div>
		</div>
	</div> 
</div>

<script>

$('#submit-selected').click(function(){
    var submitScenarios = [];
	var scenariosToSubmit = [];
	
	var bcoUserIdentifier = Cookies.get('bcoUserIdentifier');

    $('#scenario-table > tbody  > tr').each(function() {
        if ($(this.firstChild.firstChild).prop('checked')) {
            submitScenarios.push($(this.childNodes[1]).text());
        }
    });
    
    if (submitScenarios.length > 0) {

		scenariosList = ""
		scenariosList = scenariosList + "<p>The following scenarios were submitted:</p><br/>";
		scenariosList = scenariosList + "<p class='scenario-submit-list'>";
		for (i = 0; i < submitScenarios.length; i++) {
			scenariosList = scenariosList + submitScenarios[i] + "<br/>";
		}
		scenariosList = scenariosList + "</p>";
		
        $('#scenariosSubmitted').html(scenariosList)

		for (i = 0; i < scenarios.length; i++) {
			scenario = scenarios[i];
			scenario.userIdentifier = bcoUserIdentifier;
			scenario.outputType = $('#output-type').find(":selected").val();
			if ($.inArray(scenario.scenarioName, submitScenarios) > -1) {
				scenariosToSubmit.push(scenario);
			}
		}

        $.ajax({
            method: 'POST',
            data: JSON.stringify(scenariosToSubmit),
            url: '/process-scenarios',
            success: function(data) {
              $('#result').text(data);
            }
          });

     } else {
     	scenariosList = "<p>No scenarions were selected</p>";
        $('#scenariosSubmitted').html(scenariosList)
     }
	
	$("#scenariosSubmittedListModal").modal()
   
    return false;
});

</script>

</body>

</html>