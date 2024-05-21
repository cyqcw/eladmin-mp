from playwright.sync_api import Playwright, sync_playwright

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
        print(f"{username}:{password} 获得的提示为 {alert_text}")
        # 设置检查点，检查文本内容
        assert expect in alert_text, f"Alert文本内容不符合预期，实际为: {alert_text}"
    except:
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

