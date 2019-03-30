/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee.postcode;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class PostcodeProxy {

    private String url;
    private String origin;
    private String userAgent;

    @SneakyThrows
    private String endpoint(String query, int page) {
        return String.format("%s%s&cpage=%d&origin=%s", url, URLEncoder.encode(query, "UTF-8"), page, URLEncoder.encode(origin, "UTF-8"));
    }

    private Postcodes doParse(Document html, int page) {
        Elements listPost = html.select(".list_post");
        int totalCount = NumberUtils.toInt(listPost.attr("data-totalcount"), 0);
        int listSize = NumberUtils.toInt(listPost.attr("data-listsize"), 0);

        List<Postcode> list = new ArrayList<>(listSize);

        if (totalCount > 0 && listSize > 0) {
            Elements li = listPost.select("li");
            Iterator<Element> it = li.iterator();
            while (it.hasNext()) {
                Element e = it.next();
                Postcode.Builder builder = Postcode.builder();
                builder.zonecode(StringUtils.defaultIfEmpty(e.attr("data-zonecode"), null))
                        .postcode(StringUtils.defaultIfEmpty(e.attr("data-postcode"), null))
                        .sido(StringUtils.defaultIfEmpty(e.attr("data-sido"), null))
                        .sigungu(StringUtils.defaultIfEmpty(e.attr("data-sigungu"), null))
                        .sigunguCode(StringUtils.defaultIfEmpty(e.attr("data-sigungu_code"), null))
                        .bcode(StringUtils.defaultIfEmpty(e.attr("data-bcode"), null))
                        .bname(StringUtils.defaultIfEmpty(e.attr("data-bname"), null))
                        .bname1(StringUtils.defaultIfEmpty(e.attr("data-bname1"), null))
                        .bname2(StringUtils.defaultIfEmpty(e.attr("data-bname2"), null))
                        .hname(StringUtils.defaultIfEmpty(e.attr("data-hname"), null))
                        .buildingCode(StringUtils.defaultIfEmpty(e.attr("data-building_code"), null))
                        .buildingName(StringUtils.defaultIfEmpty(e.attr("data-building_name"), null))
                        .roadname(StringUtils.defaultIfEmpty(e.attr("data-roadname"), null))
                        .roadnameCode(StringUtils.defaultIfEmpty(e.attr("data-roadname_code"), null));

                Elements addresses = e.select("dl dd span.txt_address");
                Iterator<Element> it2 = addresses.iterator();
                List<String> jibunAddresses = new ArrayList<>();
                while (it2.hasNext()) {
                    Element e2 = it2.next();
                    String address = e2.attr("data-addr");
                    AddressType addressType = AddressType.valueOf(e2.attr("data-addr_type"));
                    if (StringUtils.isNotEmpty(address)) {
                        if (addressType == AddressType.R) {
                            builder.roadAddress(address);
                        }
                        else {
                            if (!jibunAddresses.contains(address))
                                jibunAddresses.add(address);
                        }
                    }
                }
                builder.jibunAddresses(jibunAddresses);
                list.add(builder.build());
            }
        }

        return new Postcodes(totalCount, page, list);
    }

    public Postcodes search(String query, int page) throws IOException {
        Document html = Jsoup.connect(endpoint(query, page))
                .userAgent(userAgent)
                .get();
        return doParse(html, page);
    }

}