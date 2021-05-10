<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>BenMAP Global Online - About</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="/css/custom.css">
     <script src="/js/jquery/jquery.min.js"></script>
   <script src="/js/bootstrap/bootstrap.min.js"></script>
</head>
<body>

	<#include 'navigation.ftl'>

    <div class="container-fluid" style="padding: 25px 25px 25px 25px;">

       <div class="row section-padding">
			<div class="col-12">
			<img src="/images/BenMAP-Global-Online.png" class="homepage-img"/>
			</div>
		</div>

        <div class="row section-title">
			<div class="col-12">
				About the Tool
			</div>
		</div>

        <div class="row section-padding">
			<div class="col-12">
				The GBD Rollback tool in BenMAP Global Online allows you to select a country, region, or 
				group of countries and see either the current health burden associated with 
				PM2.5 concentrations in that area or the health benefits of potential reductions 
				in those PM2.5 levels. It is based on data from the 2013 and 2015 
				Global Burden of Disease (GBD) conducted by IHME. The outputs include the baseline 
				and policy case PM2.5 concentrations as well as the population-weighted air quality change.
			</div>
		</div>

       <div class="row section-title">
			<div class="col-12">
				How it Works
			</div>
		</div>

        <div class="row section-padding">
			<div class="col-12">
				<p>
				The GBD Rollback tool uses data from the 2013 and 2015 GBD study to allow users to estimate the human health burden of PM2.5 levels in each country as well as the benefits of reducing these air pollution levels. Users can “roll back,” or adjust ambient PM2.5 levels in one or more countries or regions and calculate the total burden, or avoided deaths, in that region. The tool also estimates avoided life years lost, changes in life expectancy, and the economic benefits associated with avoided deaths.
				</p>
				<p>
				GBD Rollback Tool results are exported as .xlsx files, which may be viewed in a spreadsheet tool such as Excel (there is also a .csv option).
				</p>
				
				<p>
				The tool uses a grid with 0.1 degree resolution (approximately 10km) grid cells and includes:
				<ul>
					<li>
				2015 PM2.5 pollution concentrations.  Negative concentrations were adjusted to zero.
					</li>
					<li>
				2015 global population  data stratified by age and gender. Elder populations were combined into an "80UP" age group to align with the incidence dataset.
					</li>
					
					
					<li>
				2013 mortality incidence  for six health endpoints (COPD, cerebrovascular disease, ischemic heart disease, lung cancer, acute lower respiratory infection, and non-accidental) stratified by age and gender.  Neonatal ("0 to 0") and "1 to 4" age groups were combined into a "0 to 4" age group to align with population data.
					</li>
					<li>
				The 2013 Integrated Exposure Response (IER) function  employed by the 2013 GBD study to estimate premature mortality associated with ambient air pollution. The 2013 IER function estimates premature mortality from COPD, cerebrovascular disease, ischemic heart disease, and lung cancer.
					</li>
					<li>
				The Shape-Constrained Health Impact Function (SCHIF) developed by Burnett et al. (in preparation) . Like the IER, the SCHIF is a meta-analytic concentration-response function developed using data from many PM2.5-related epidemiological studies. Depending on the country being evaluated, the SCHIF estimates draw from mortality incidence from either (a) all non-accidental causes, or (b) a “re-attributed” incidence rate representing deaths due to COPD, cerebrovascular disease, ischemic heart disease, lung cancer, and acute lower respiratory infection).
					</li>

				</ul>
				</p>

			</div>
		</div>

      <div class="row section-title">
			<div class="col-12">
				For More Information
			</div>
		</div>

        <div class="row section-padding">
			<div class="col-12">
				<p><a href="#" class="about-link">BenMAP User’s Guide, Chapter 9</a></p>
				<p><a href="#" class="about-link">IHME GBD Website</a></p>
			</div>
		</div>

		<hr/>

		<div class="row section-padding citation">
			<div class="col-12">
			
			<p>
		  		<sup>1</sup> Cohen, A. J.; Brauer, M.; Burnett, R. T. (2017). 
		  		Estimates and 25-year trends of the global burden of disease attributable to 
		  		ambient air pollution: an analysis of data from the Global Burden of Diseases Study 2015. 
		  		Lancet, 389(10082), 1907-18.
			</p>

			<p>
				<sup>2</sup> Center for International Earth Science Information Network - CIESIN - 
				Columbia University. 2016. Gridded Population of the World, Version 4 (GPWv4): 
				Administrative Unit Center Points with Population Estimates. Palisades, NY: 
				NASA Socioeconomic Data and Applications Center (SEDAC). 
				<a href="http://dx.doi.org/10.7927/H4F47M2C">http://dx.doi.org/10.7927/H4F47M2C</a>.
			</p>

			<p>
				<sup>3</sup> Global Burden of Disease Study 2013. Global Burden of Disease Study 2013 (GBD 2013) 
				Incidence, Prevalence, and Years Lived with Disability 1990-2013. Seattle, United States: 
				Institute for Health Metrics and Evaluation (IHME), 2015.  
				<a href="http://ghdx.healthdata.org/record/global-burden-disease-study-2013-gbd-2013-incidence-prevalence-and-years-lived-disability">
				http://ghdx.healthdata.org/record/global-burden-disease-study-2013-gbd-2013-incidence-prevalence-and-years-lived-disability</a>
			</p>

			<p>
				<sup>4</sup> Burnett RT, Pope CA, Ezzati M, Olives C, Lim SS, Mehta S, Shin HH, Singh G, 
				Hubbell B, Brauer M, Anderson HR, Smith KR, Balmes JR, Bruce NG, Kan H, Laden F, 
				Pruss-Ustun A, Turner MC, Gapstur SM, Diver WR, Cohen A. (2014). 
				An integrated risk function for estimating the global burden of disease attributable 
				to ambient fine particulate matter exposure. Environ Health Perspect 122:397-403; 
				<a href="http://dx.doi.org/10.1289/ehp.1307049">http://dx.doi.org/10.1289/ehp.1307049</a>
			</p>

			<p>
				<sup>5</sup> Burnett RT, Chen H, Szyszkowicz M, Fann N, Hubbell B, Pope CA, Apte JS, 
				Brauer M, Cohen A, Weichenthal S, Coggins J, Di Q, Brunekreef B, Frostad J, Lim SS, Kan H, 
				Pruss-Ustun AM, AARP collaborators, ACS collaborators, CTS collaborators, 
				Canadian Breast Screening collaborators, CanCHEC/CCHS collaborators, 
				Chinese Male Cohort Collaborators, CUELS collaborators, English Cohort collaborators, 
				Hong Kong collaborators, NHIS collaborators, NHS collaborators, 
				Rome Census Cohort collaborators, VHM&PP collaborators. 2017. 
				A new approach to estimating global mortality burden from outdoor fine particle exposure. 
				Manuscript in preparation.
			</p>
						
			</div>
		</div>
		
 	</div>

</body>
</html>
