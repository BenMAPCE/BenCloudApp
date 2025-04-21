import { date } from "quasar";

export const convertToUserTimezone = (dateString) => {
    const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const formattedDate = date.formatDate(dateString, 'YYYY-MM-DD HH:mm:ss', {
        timeZone: userTimezone
    });
    return `${formattedDate} (${userTimezone})`;
}