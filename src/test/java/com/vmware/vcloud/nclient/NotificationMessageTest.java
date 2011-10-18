package com.vmware.vcloud.nclient;

import static org.testng.Assert.assertEquals;

import java.util.Calendar;

import org.testng.annotations.Test;

@Test(groups = {"unit"})
public class NotificationMessageTest {

    @Test
    public void testParsing() throws Exception {
        String payload = getPayload();
        NotificationMessage msg = NotificationMessage.createFromPayloadAndHeaders(payload, null);
        assertEquals(msg.getType(), "com/vmware/vcloud/event/blockingtask/create");
        assertEquals(msg.getEntityType(), "blockingTask");
        assertEquals(msg.getEntityName(), "vappUpdateVm");
        assertEquals(msg.getEntityHref(), "https://10.23.6.35/api/entity/urn:vcloud:blockingTask:41aaf964-7452-42e6-9b8d-772e5f8421d8");
        assertEquals(msg.getOrgName(), "Default");
        assertEquals(msg.getUserName(), "vcloud");
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 5, 10, 10, 4, 49);
        cal.set(Calendar.MILLISECOND, 47);
        assertEquals(msg.getTimestamp(), cal.getTime());
    }

    String getPayload() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
        		"<vmext:Notification xmlns:vmext=\"http://www.vmware.com/vcloud/extension/v1.5\" type=\"com/vmware/vcloud/event/blockingtask/create\" eventId=\"55aba16e-427c-431a-9949-00db6aa78e5a\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/extension/v1.5 http://10.23.6.35/api/v1.5/schema/vmwextensions.xsd\">\r\n" +
        		"    <vmext:Link rel=\"entityResolver\" href=\"https://10.23.6.35/api/entity/\"/>\r\n" +
        		"    <vmext:EntityLink rel=\"entity\" type=\"vcloud:blockingTask\" name=\"vappUpdateVm\" id=\"urn:vcloud:blockingTask:41aaf964-7452-42e6-9b8d-772e5f8421d8\"/>\r\n" +
        		"    <vmext:EntityLink rel=\"down\" type=\"vcloud:user\" name=\"vcloud\" id=\"urn:vcloud:user:74a39795-2307-4667-ba2a-2eddc7e52325\"/>\r\n" +
        		"    <vmext:EntityLink rel=\"up\" type=\"vcloud:org\" name=\"Default\" id=\"urn:vcloud:org:251ae0f6-9780-429d-a8cf-9495c036eef2\"/>\r\n" +
        		"    <vmext:EntityLink rel=\"task\" type=\"vcloud:task\" name=\"vappUpdateVm\" id=\"urn:vcloud:task:a56be2f7-2cb4-4561-a7ef-ee2fdd2d3a15\"/>\r\n" +
        		"    <vmext:EntityLink rel=\"task:owner\" type=\"vcloud:vm\" id=\"urn:vcloud:vm:26839d04-5050-4702-a602-0667be86dad6\"/>\r\n" +
        		"    <vmext:Timestamp>2011-06-10T10:04:49.047+03:00</vmext:Timestamp>\r\n" +
        		"    <vmext:OperationSuccess>true</vmext:OperationSuccess>\r\n" +
        		"</vmext:Notification>";
    }

}
