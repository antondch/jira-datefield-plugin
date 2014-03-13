package ru.dch.jira.customfields;

//import com.atlassian.jira.issue.Issue;
//import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
//import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
//import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
//import com.atlassian.jira.issue.fields.CustomField;
//import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
//import com.atlassian.jira.web.action.util.CalendarResourceIncluder;
//
//import java.util.Map;
//
///**
// * Created by Jessie on 13.03.14.
// */
//public class DateWithoutOutForwardingCF extends GenericTextCFType
//{
//    public DateWithoutOutForwardingCF(CustomFieldValuePersister customFieldValuePersister,
//                                      GenericConfigManager genericConfigManager)
//    {
//        super(customFieldValuePersister, genericConfigManager);
//    }
//
//    @Override
//    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
//    {
//        final Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
//        params.put("calendarIncluder", new CalendarResourceIncluder());
//        return params;
//    }
//}
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.imports.project.customfield.*;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.converters.DatePickerConverter;
import com.atlassian.jira.issue.customfields.impl.AbstractCustomFieldType;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.DateField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.*;
import com.atlassian.jira.issue.fields.rest.json.*;
import com.atlassian.jira.issue.history.DateTimeFieldChangeLogHelper;
import com.atlassian.jira.rest.Dates;
import com.atlassian.jira.util.DateFieldFormat;
import com.atlassian.util.concurrent.LazyReference;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

// Referenced classes of package com.atlassian.jira.issue.customfields.impl:
//            AbstractSingleFieldType, FieldValidationException, AbstractCustomFieldType

public class DateWithoutOutForwardingCF extends AbstractSingleFieldType implements SortableCustomField, ProjectImportableCustomField
{
//    /* member class not found */
//    class LazyIso8601DateFormatter {}
//
//    /* member class not found */
//    class Visitor {}


    public DateWithoutOutForwardingCF(CustomFieldValuePersister customFieldValuePersister, DatePickerConverter dateConverter, GenericConfigManager genericConfigManager, DateTimeFieldChangeLogHelper dateTimeFieldChangeLogHelper, DateFieldFormat dateFieldFormat, DateTimeFormatterFactory dateTimeFormatterFactory)
    {
        super(customFieldValuePersister, genericConfigManager);
//        iso8601Formatter = new LazyIso8601DateFormatter(null);
        this.dateConverter = dateConverter;
        this.dateTimeFieldChangeLogHelper = dateTimeFieldChangeLogHelper;
        projectCustomFieldImporter = new NoTransformationCustomFieldImporter();
        this.dateFieldFormat = dateFieldFormat;
        this.dateTimeFormatterFactory = dateTimeFormatterFactory;
    }

//    /**
//     * @deprecated Method DateCFType is deprecated
//     */
//
//    public DateCFType(CustomFieldValuePersister customFieldValuePersister, DatePickerConverter dateConverter, GenericConfigManager genericConfigManager)
//    {
//        this(customFieldValuePersister, dateConverter, genericConfigManager, (DateTimeFieldChangeLogHelper)ComponentAccessor.getComponentOfType(com/atlassian/jira/issue/history/DateTimeFieldChangeLogHelper), (DateFieldFormat)ComponentAccessor.getComponentOfType(com/atlassian/jira/util/DateFieldFormat), (DateTimeFormatterFactory)ComponentAccessor.getComponentOfType(com/atlassian/jira/datetime/DateTimeFormatterFactory));
//    }

    protected PersistenceFieldType getDatabaseType()
    {
        return PersistenceFieldType.TYPE_DATE;
    }

    protected Object getDbValueFromObject(Date customFieldObject)
    {
        return customFieldObject;
    }

    protected Date getObjectFromDbValue(Object databaseValue) throws FieldValidationException
    {
        return (Timestamp)databaseValue;
    }

    public String getChangelogString(CustomField field, Date value)
    {
        if(value == null)
            return "";
        else
            return getStringFromSingularObject(value);
    }

    public String getChangelogValue(CustomField field, Date value)
    {
        if(value == null)
            return "";
        else
            return dateTimeFieldChangeLogHelper.createChangelogValueForDateField(value);
    }

    public String getStringFromSingularObject(Date customFieldObject)
    {
        return dateConverter.getString(customFieldObject);
    }

    public Date getSingularObjectFromString(String string) throws FieldValidationException
    {
        return dateConverter.getTimestamp(string);
    }

    public int compare(Date v1, Date v2, FieldConfig fieldConfig)
    {
        return v1.compareTo(v2);
    }

    public Date getDefaultValue(FieldConfig fieldConfig)
    {
        Date defaultValue = (Date)genericConfigManager.retrieve("DefaultValue", fieldConfig.getId().toString());
        if(isUseNow(defaultValue))
            defaultValue = new Timestamp((new Date()).getTime());
        return defaultValue;
    }

    public boolean isUseNow(Date date)
    {
        return DatePickerConverter.USE_NOW_DATE.equals(date);
    }

    public boolean isUseNow(FieldConfig fieldConfig)
    {
        Date defaultValue = (Date)genericConfigManager.retrieve("DefaultValue", fieldConfig.getId().toString());
        return isUseNow(defaultValue);
    }

    public String getNow()
    {
        return dateConverter.getString(new Date());
    }

    public ProjectCustomFieldImporter getProjectImporter()
    {
        return projectCustomFieldImporter;
    }

    public Map getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map velocityParameters = super.getVelocityParameters(issue, field, fieldLayoutItem);
        velocityParameters.put("dateFieldFormat", dateFieldFormat);
//        velocityParameters.put("iso8601Formatter", iso8601Formatter.get());
        return velocityParameters;
    }

//    public Object accept(AbstractCustomFieldType.VisitorBase visitor)
//    {
//        if(visitor instanceof Visitor)
//            return ((Visitor)visitor).visitDate(this);
//        else
//            return super.accept(visitor);
//    }

    public FieldTypeInfo getFieldTypeInfo(FieldTypeInfoContext fieldTypeInfoContext)
    {
        return new FieldTypeInfo(null, null);
    }

    public JsonType getJsonSchema(CustomField customField)
    {
        return JsonTypeBuilder.custom("date", getKey(), customField.getIdAsLong());
    }

    public FieldJsonRepresentation getJsonFromIssue(CustomField field, Issue issue, boolean renderedVersionRequested, FieldLayoutItem fieldLayoutItem)
    {
        Date date = (Date)getValueFromIssue(field, issue);
        if(date == null)
            return new FieldJsonRepresentation(new JsonData(null));
        FieldJsonRepresentation pair = new FieldJsonRepresentation(new JsonData(Dates.asDateString(date)));
        if(renderedVersionRequested)
            pair.setRenderedData(new JsonData(dateFieldFormat.format(date)));
        return pair;
    }

//    public RestFieldOperationsHandler getRestFieldOperation(CustomField field)
//    {
//        return new DateCustomFieldOperationsHandler(field, dateFieldFormat, getI18nBean());
//    }



    protected  Object getDbValueFromObject(Object x0)
    {
        return getDbValueFromObject((Date)x0);
    }

    public String getChangelogValue(CustomField x0, Object x1)
    {
        return getChangelogValue(x0, (Date)x1);
    }

//    public Object getDefaultValue(FieldConfig x0)
//    {
//        return getDefaultValue(x0);
//    }

    public String getChangelogString(CustomField x0, Object x1)
    {
        return getChangelogString(x0, (Date)x1);
    }

//    public Object getSingularObjectFromString(String x0)
//            throws FieldValidationException
//    {
//        return getSingularObjectFromString(x0);
//    }

    public String getStringFromSingularObject(Object x0)
    {
        return getStringFromSingularObject((Date)x0);
    }

    public int compare(Object x0, Object x1, FieldConfig x2)
    {
        return compare((Date)x0, (Date)x1, x2);
    }

    protected final DatePickerConverter dateConverter;
    private final DateTimeFieldChangeLogHelper dateTimeFieldChangeLogHelper;
    private final ProjectCustomFieldImporter projectCustomFieldImporter;
    private final DateFieldFormat dateFieldFormat;
    private final DateTimeFormatterFactory dateTimeFormatterFactory;
//    private final LazyReference iso8601Formatter;

}