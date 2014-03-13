package ru.dch.jira.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.web.action.util.CalendarResourceIncluder;

import java.util.Map;

/**
 * Created by Jessie on 13.03.14.
 */
public class DateWithoutOutForwardingCF extends GenericTextCFType
{
    public DateWithoutOutForwardingCF(CustomFieldValuePersister customFieldValuePersister,
                                      GenericConfigManager genericConfigManager)
    {

        super(customFieldValuePersister, genericConfigManager);
    }

    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        final Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("calendarIncluder", new CalendarResourceIncluder());
        return params;
    }
}
