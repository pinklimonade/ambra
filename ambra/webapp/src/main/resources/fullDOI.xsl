<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet id="full-doi.xsl" version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs">

<xsl:output method="xml" indent="yes" encoding="UTF-8"
            doctype-public="-//NLM//DTD Journal Publishing DTD v2.0 20040830//EN"
            doctype-system="http://dtd.nlm.nih.gov/publishing/2.0/journalpublishing.dtd"
            include-content-type="yes"/>

  <xsl:template match="article-meta">
    <article-meta>
      <self-uri>http://dx.plos.org/<xsl:value-of select="article-id[@pub-id-type='doi']"/></self-uri>
      <xsl:apply-templates/>
    </article-meta>
  </xsl:template>
  
  <xsl:template match="article-meta/article-id[@pub-id-type='doi']">
    <article-id pub-id-type="doi">http://dx.plos.org/<xsl:value-of select="."/></article-id>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>