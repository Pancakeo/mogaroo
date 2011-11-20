package org.mogaroo.myuw.api.adapters;

import org.mogaroo.myuw.api.AdapterException;
import org.mogaroo.myuw.api.model.CourseSection;
import org.w3c.dom.Document;

/**
 * Convert a document to a CourseSection.
 * TODO: This currently only gets enrollment info.
 * @author Sean
 *
 */
public class CourseSectionAdapter extends DocumentAdapterBase<CourseSection> {

	public CourseSection adapt(Document src) throws AdapterException {
		try {
			int currentEnrollment = Integer.parseInt(evaluate("/Section/CurrentEnrollment", src));
			int enrollmentLimit = Integer.parseInt(evaluate("/Section/LimitEstimateEnrollment", src));
			return new CourseSection(null, null, null,
					null, null, null,
					currentEnrollment, enrollmentLimit);
		}
		catch (Exception e) {
			throw new AdapterException(e);
		}
	}

}
