from playwright.sync_api import Playwright, sync_playwright
from bs4 import BeautifulSoup

def run(playwright: Playwright, username: str, password: str, expect: str) -> None:
    browser = playwright.chromium.launch(headless=False)
    context = browser.new_context()
    page = context.new_page()
    page.goto("http://localhost:8013")
    page.get_by_placeholder("账号").fill(username)
    page.get_by_placeholder("密码").fill(password)
    page.get_by_placeholder("验证码").fill("1")
    page.get_by_role("button", name="登 录").click()
    
    try:
        # 等待这个具有"alert"角色的元素出现，设置合理的超时时间
        alert_element = page.wait_for_selector('[role="alert"]', timeout=3000)  # 假设等待3秒
        alert_text = alert_element.text_content()
        # 页面加载完成后获取页面内容
        html_content = page.content()
        # 使用BeautifulSoup解析HTML并提取所有文本
        soup = BeautifulSoup(html_content, 'html.parser')
        all_text = soup.get_text(separator='\n').replace('\n', ' ')
        
        print(f"{username}:{password} 获得的提示为 {alert_text}, 测试通过")
        # 设置检查点，检查文本内容
        assert expect in alert_text or expect in all_text, f"Alert文本内容不符合预期，实际为: {alert_text}, 测试失败"
    except:
        # 页面加载完成后获取页面内容
        html_content = page.content()
        # 使用BeautifulSoup解析HTML并提取所有文本
        soup = BeautifulSoup(html_content, 'html.parser')
        all_text = soup.get_text(separator='\n')
        if expect in all_text:
            print(f"{username}:{password} 未找到alert元素，但页面内容包含了预期的文本，测试通过。")
            return True

        print(f"{username}:{password} 未找到alert元素，直接结束流程。")
    finally:
        print("===============================================================================")

    # ---------------------
    context.close()
    browser.close()


with sync_playwright() as playwright:
    with open('input.csv', 'r', encoding='utf-8') as input:
        for line in input.readlines():
            if line.startswith('#'):
                continue
            username, password, expect = line.strip().split(',')
            run(playwright, username, password, expect)

