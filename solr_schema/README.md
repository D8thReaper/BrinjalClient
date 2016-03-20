## Synopsis

Schema for the search functionality of discover implemented on top of retrieve and rank api of IBM Watson.

## Code Example
 
  `<field name="place" indexed="true" type="watson_text_en"  stored="true"/>
  <field name="subcategory" indexed="true" type="watson_text_en"/>
  <field name="tags" indexed="true" type="watson_text_en"/>
  <field name="address" indexed="true" type="watson_text_en"/>
  <field name="coord" type="location" indexed="true" stored="false"/>`


## Motivation

Search functionality implemented using solr.
Rank will be implemented in the near future.


## Usage

Any of the above mentioned parameters passed to the cluster generate suitable results which are then sent to the app as json.


## Contributors

@hkhatri241.
@d8threaper
@lakshit1001

