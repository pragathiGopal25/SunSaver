# Frost Guide 

## Observation data that we can use:
   -  `sum(duration_of_sunshine%20P1D)`
     - Data description: Hvor mange timer med sol det har vært det siste døgnet
   -  `sum(duration_of_sunshine_normal P1M 1991_2020)`
     - Data description: Månedsnormal for solskinnstid beregnet for normalperioden 1991-2020. 
   -  `mean(solar_irradiance PT1H) - Only collects data in Bergen`
   -  `mean(cloud_area_fraction P1D):`
     - Data description: Døgnmiddel for skydekke. Middelverdien er et aritmetisk gjennomsnitt av tre daglige observasjoner (kl. 06, 12 og 18 UTC).
     - Values:  Samla skydekke registreres med kodetall 0-8 som sier hvor mange åttendeler av himmelen som er skydekt 
     - (0=skyfritt, 8=helt overskya himmel. Kode -3 eller 9 = mengden av skyer kan ikke bedømmes pga. tåke, snøfokk eller liknende. -3 presenteres som ".")
   - `mean(air_temperature P1M) `
     - Data description: average air temp for each month
   - `mean(snow_coverage_type P1M)`
     - Data description: Månedsmiddel av snødekke
     - Values: Middelverdien er et aritmetisk gjennomsnitt av daglige verdier. 
     - Snødekke registreres med kodetall 0-4. 1=mest bar mark. 2=like mye snødekt som bar mark. 3=mest snødekt mark. 4=snø overalt. 
     - Kode=0 og -1 betyr "ikke snø" (-1 presenteres som ".").


NOTE: %20 to replace the space for urls.

## Reference time formats 
NOTE: format for reference time is [check](https://frost.met.no/concepts2.html#time_specification) for more):
  -  "latest"  -> for latest data, however this does not consistently provide data
  -  date: YYYY-MM-DD
  -  time: hh:mm:ss
  -  time: hh:mm:ss
  -  interval (from, to): <datetime>/<datetime>
