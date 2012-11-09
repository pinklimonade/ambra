<div dojoType="dijit.Dialog" id="SavedSearch">

  <div class="top-header">
    <span class="heading">Save this search</span>
  </div>

  <div class="body-wrapper">
    Save search as <br/>
    <input type="text" class="big-box" id="text_name_savedsearch"/><br/>

    <div class="check-box"><strong>Create an email alert:</strong>
      <input type="checkbox" checked id="cb_weekly_savedsearch"/>Weekly
      <input type="checkbox" id="cb_monthly_savedsearch"/>Monthly
    </div>

    <div class="grey-text">You can change these options in your Preferences&gt; Search Alerts</div>

    <div class="button-wrapper">
      <input type="button" id="btn_save_savedsearch" value="Save"/>
      <input type="button" id="btn_cancel_savedsearch" value="Cancel"/>
      <span class="errortext" id="span_error_savedsearch"></span>
    </div>
  </div><!--end body-wrapper-->

</div><!--end dijit.dialog-->
